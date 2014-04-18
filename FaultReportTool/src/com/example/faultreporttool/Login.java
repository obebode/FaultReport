package com.example.faultreporttool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class Login extends CustomComponent implements View,

Button.ClickListener {

    // Field variables of the login view
    public static final String NAME = "LOGIN";
    private final TextField username;
    private final PasswordField password;
    private final Button loginButton;

    private Connection connect = null;
    private final Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private String query;

    // Constructor
    public Login() {

        setSizeFull();

        // Create the username input field and validation requirements
        username = new TextField("Username");
        username.setWidth("300px");
        username.setRequired(true);
        username.setInputPrompt("Your username (eg. obebode@gmail.com)");
        username.addValidator(new EmailValidator(
                "Username must be Email address format"));
        username.setInvalidAllowed(false);

        // Create the password input field and set validation requirements
        password = new PasswordField("Password");
        password.setWidth("300px");
        password.addValidator(new PasswordValidator());
        password.setRequired(true);
        password.setValue("");
        password.setNullRepresentation(null);

        // Create login button
        loginButton = new Button("Login", this);

        // Add the fields to a panel
        VerticalLayout fields = new VerticalLayout(username, password,
                loginButton);
        fields.setCaption("PLEASE LOGIN");
        fields.setSpacing(true);
        fields.setMargin(new MarginInfo(true, true, true, false));
        fields.setSizeUndefined();

        // Create the View root layout to hold or contain the three fields
        VerticalLayout viewLayout = new VerticalLayout(fields);
        viewLayout.setSizeFull();
        viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
        viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);
        setCompositionRoot(viewLayout);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // Focus the username field when the user arrives
        username.focus();
    }

    private static final class PasswordValidator extends
            AbstractValidator<String> {

        public PasswordValidator() {
            super("The password provided is not valid");
        }

        @Override
        protected boolean isValidValue(String value) {
            //
            // Password must be at least 8 characters long and contain at least
            // one number
            //
            if (value != null
                    && (value.length() < 8 || !value.matches(".*\\d.*"))) {
                Notification
                        .show("Password must be atleast 8 characters and contain atleast one number");
                return false;
            }
            return true;
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {

        /*
         * Validates the fields by using navigator. Both the username and
         * password are checked before the login information is set to the
         * database for verification.
         */

        if (!username.isValid() || !password.isValid()) {
            return;
        }

        String user_name = username.getValue();
        String user_password = password.getValue();

        try {

            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FaultReportTool", "root", "");

            query = "SELECT Username, Password FROM userid WHERE (Username = ? AND Password = ?)";

            preparedStatement = connect.prepareStatement(query);

            preparedStatement.setString(1, user_name);
            preparedStatement.setString(2, user_password);
            resultSet = preparedStatement.executeQuery();

            resultSet.next();
            String Check_Username = resultSet.getString(1);
            String Check_Password = resultSet.getString(2);

            boolean isValid = Check_Username.equals(user_name)
                    && Check_Password.equals(user_password);

            if (isValid) {
                getSession().setAttribute("username", user_name);
                getUI().getNavigator().navigateTo(FaultReportMainView.NAME);
                Notification.show("Database successfully authenticated!");
            }

            else {
                Notification
                        .show("Something is wrong with username or password");
                this.password.setValue(null);
                this.password.focus();
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        } finally {
            close();
        }

    }

    private void close() {
        // TODO Auto-generated method stub
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }

}
