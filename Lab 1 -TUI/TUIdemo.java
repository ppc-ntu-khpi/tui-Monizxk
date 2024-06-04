package com.mybank.tui;

import com.mybank.data.DataSource;
import com.mybank.domain.Account;
import com.mybank.domain.Bank;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;
import jexer.*;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;


import java.util.Locale;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(new Locale("en","us"));

        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        new DataSource("./data/test.dat").loadData();

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu  

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu 

        setFocusFollowsMouse(true);
        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        TWindow custWin = addWindow("Customer Window", 2, 1, 40, 10, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter valid customer number and press Show...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        TField custNo = custWin.addField(24, 2, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 4, 38, 8);
        custWin.addButton("&Show", 28, 2, new TAction() {
            @Override
            public void DO() {
            try {
                int custNum = Integer.parseInt(custNo.getText());
                Customer customer = Bank.getCustomer(custNum);
                Account account = customer.getAccount(0);
                String type;

                if (account instanceof SavingsAccount) {
                    type = "SavingsAccount";
                } else {
                    type = "CheckingAccount";
                }

                //details about customer with index==custNum
                details.setText(
                    String.format(
                        "Owner Name: %s %s (id=%d)\nAccount Type: %s\nAccount Balance: $%.2f",
                        customer.getFirstName(), customer.getLastName(), custNum, type, account.getBalance()
                    )
                );
            } catch (Exception e) {
                messageBox("Error", "You must provide a valid customer number!").show();
            }
            }
        });
    }
}
