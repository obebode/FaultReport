package com.example.faultreporttool;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("faultreporttool")
public class FaultreporttoolUI extends UI {

    Navigator navigator;
    protected static final String MAINVIEW = "main";

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = FaultreporttoolUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();

        // Create a navigator to control views
        navigator = new Navigator(this, this);

        // Initial log view where the user can log into the application
        getNavigator().addView(Login.NAME, Login.class);

        // Add main of the application
        getNavigator().addView(FaultReportMainView.NAME,
                FaultReportMainView.class);

        // Add view change handler to make sure that the view is always
        // redirected to the login view if the
        // user is not logged in.
        getNavigator().addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                // Check if a user has logged in
                boolean isLoggedIn = getSession().getAttribute("username") != null;
                boolean isLoginView = event.getNewView() instanceof Login;

                if (!isLoggedIn && !isLoginView) {
                    // Redirect to login view always if a user has not yet
                    // logged in
                    getNavigator().navigateTo(Login.NAME);
                    return false;

                } else if (isLoggedIn && isLoginView) {
                    // If someone tries to access to login view while logged in,
                    // then cancel
                    return false;
                }

                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {

            }
        });

    }
}