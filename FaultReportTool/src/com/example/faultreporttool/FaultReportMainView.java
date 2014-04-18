package com.example.faultreporttool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.vaadin.data.Validator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class FaultReportMainView extends CustomComponent implements View {

    public final static String NAME = "";
    Label text = new Label();

    public final TextField NameOfReporter;
    public final TextField LastName;
    public final TextField AddressOfReporter;
    public final TextField PhoneNumber;
    public final TextField EmailOfReporter;
    public final TextField NameOfOrganisation;
    public final TextField FaultTitle;
    public final TextArea FaultDescription;
    Upload upload;
    private final Button SubmitButton;

    // Fields to store items in the database
    String name_reporter;
    String last_name;
    String address_reporter;
    String phone_number;
    String user_email;
    String nameof_organisation;
    String fault_title;
    String fault_description;

    // Database SQL field variables
    private Connection connect = null;
    private final Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private final ResultSet resultSet = null;
    private String query;

    public FaultReportMainView() {
        // setCompositionRoot(new CssLayout(text, logout));

        NameOfReporter = new TextField("First Name");
        NameOfReporter.setRequired(true);
        NameOfReporter.setWidth("300px");
        NameOfReporter.setInvalidAllowed(false);
        NameOfReporter.addValidator(new NameValidator());
        NameOfReporter.setImmediate(true);

        LastName = new TextField("Last Name");
        LastName.setRequired(true);
        LastName.setWidth("300px");
        LastName.setInvalidAllowed(false);
        LastName.addValidator(new NameValidator());
        LastName.setImmediate(true);

        AddressOfReporter = new TextField("Address");
        AddressOfReporter.setRequired(true);
        AddressOfReporter.setWidth("300px");
        AddressOfReporter.setInvalidAllowed(false);

        NameOfOrganisation = new TextField("Name of Organisation");
        NameOfOrganisation.setRequired(false);
        NameOfOrganisation.setWidth("300px");

        FaultTitle = new TextField("Fault Title");
        FaultTitle.setRequired(true);
        FaultTitle.setWidth("300px");

        FaultDescription = new TextArea("Fault Description");
        FaultDescription.setRequired(true);
        FaultDescription.setWidth("300px");
        FaultDescription.setHeight("300px");

        EmailOfReporter = new TextField("Email");
        EmailOfReporter.setRequired(true);
        EmailOfReporter.setWidth("300px");
        EmailOfReporter.setInvalidAllowed(false);
        EmailOfReporter.addValidator(new EmailValidator());
        EmailOfReporter.setImmediate(true);

        PhoneNumber = new TextField("Phone Number");
        PhoneNumber.setWidth("300px");
        PhoneNumber.setRequired(true);
        PhoneNumber.setInvalidAllowed(false);
        PhoneNumber.addValidator(new PhoneNumberValidator());

        SubmitButton = new Button("Submit");

        // Add the fault report fields to the panel
        VerticalLayout Faultfields = new VerticalLayout(NameOfReporter,
                LastName, AddressOfReporter, PhoneNumber, EmailOfReporter,
                NameOfOrganisation, FaultTitle, FaultDescription, SubmitButton);
        Faultfields.setCaption("New Service Request");
        Faultfields.setSpacing(true);
        Faultfields.setMargin(new MarginInfo(true, true, true, false));
        Faultfields.setSizeUndefined();

        // Create the View root layout to hold or contain all the fields
        VerticalLayout viewLayout = new VerticalLayout(Faultfields);
        viewLayout.setSizeFull();
        viewLayout.setComponentAlignment(Faultfields, Alignment.MIDDLE_CENTER);
        viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);
        setCompositionRoot(viewLayout);

        Button logout = new Button("Logout", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                // "Logout" the user
                getSession().setAttribute("username", null);

                // Refresh this view, should redirect to login view
                getUI().getNavigator().navigateTo(NAME);

            }
        });

        viewLayout.addComponentAsFirst(logout);

        SubmitButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // TODO Auto-generated method stub
                Notification.show("Thank You Sir");
                if (!NameOfReporter.isValid() && !AddressOfReporter.isValid()
                        && !PhoneNumber.isValid() && !EmailOfReporter.isValid()) {
                    return;
                }

                name_reporter = NameOfReporter.getValue();
                last_name = LastName.getValue();
                address_reporter = AddressOfReporter.getValue();
                phone_number = PhoneNumber.getValue();
                user_email = EmailOfReporter.getValue();
                nameof_organisation = NameOfOrganisation.getValue();
                fault_title = FaultTitle.getValue();
                fault_description = FaultDescription.getValue();

                // Insert the user entered values into the database

                try {

                    Class.forName("com.mysql.jdbc.Driver");
                    connect = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/FaultReportTool",
                            "root", "");
                    query = "INSERT INTO faultreport(FName,LName,Adress, PNumber,Email, NOrganisation,FTitle, FDescription) VALUES (?,?,?,?,?,?,?,?)";
                    preparedStatement = connect.prepareStatement(query);

                    preparedStatement.setString(1, name_reporter);
                    preparedStatement.setString(2, last_name);
                    preparedStatement.setString(3, address_reporter);
                    preparedStatement.setString(4, phone_number);
                    preparedStatement.setString(5, user_email);
                    preparedStatement.setString(6, nameof_organisation);
                    preparedStatement.setString(7, fault_title);
                    preparedStatement.setString(8, fault_description);
                    preparedStatement.executeUpdate();

                } catch (Exception e) {

                    System.out.println("ERROR: " + e);

                } finally {
                    close();
                }

            }
        });

    }

    private class EmailValidator implements Validator {

        // This is to test if the entered by the user is valid
        public boolean isValid(Object value) {
            try {
                validate(value);
                return true;
            } catch (Validator.InvalidValueException e) {
                return false;
            }

            // return true;
        }

        // Test the value using regular expression
        public void validate(Object value)
                throws Validator.InvalidValueException {

            if (value instanceof String && value != null) {
                String email = (String) value;

                /*
                 * Check the email string against regular expression matching
                 * the email format
                 */
                if (!email
                        .matches("^([a-zA-Z0-9_\\.\\-+])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4}$)")) {
                    throw new Validator.InvalidValueException(
                            "The e-mail address provided is not valid!");
                }

                else {

                }

            }
        }
    }

    class NameValidator implements Validator {

        @Override
        public void validate(Object value)
                throws Validator.InvalidValueException {

            if (value instanceof String) {
                String name = (String) value;
                if (!name.matches("^[a-zA-Z]+$")) {
                    throw new Validator.InvalidValueException(
                            "The name provided is not valid");
                }

                else {
                    // Save the information in the database
                }
            }

        }
    }

    class PhoneNumberValidator implements Validator {

        @Override
        public void validate(Object value)
                throws Validator.InvalidValueException {

            if (value instanceof String) {
                String PhoneNumber = (String) value;
                if (!PhoneNumber.matches("^[+][0-9]{10,13}$")) {
                    throw new Validator.EmptyValueException(
                            "The phone number is not valid");
                }

            }
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

    @Override
    public void enter(ViewChangeEvent event) {
        // Get the user name from the session
        String username = String.valueOf(getSession().getAttribute("username"));
        // And show the username
        text.setValue("Hello " + username);
    }

}
