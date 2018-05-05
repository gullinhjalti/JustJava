package be.runesoft.dev.justjava;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {
    int numberOfCoffees = 0;
    int coffeePrice = 2;
    Double totalPrice = 0.00;
    int promoNumber = 10;
    int promoDiscount = 5;
    boolean promoEnabled = false;
    String amountDue = "";
    boolean hasOpt1 = false;
    Double opt1Price = 0.15;
    boolean hasOpt2 = false;
    Double opt2Price = 0.20;
    String orderSummary = "";
    String userName = "Anonymous";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editText = findViewById(R.id.userNameField);
        View.OnFocusChangeListener ofc = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    }
                }
            }
        } ;
        editText.setOnFocusChangeListener(ofc);
    }
    public void incrementOrder(View view){
        clearFocusNameField();
        numberOfCoffees += 1;
        if ((numberOfCoffees >= 5) && (numberOfCoffees <= 10)){
            int numberLeft = promoNumber - numberOfCoffees;
            String promoMessage;
            if(numberLeft > 0){
                promoMessage = "You are " + numberLeft + " coffees away from a " + promoDiscount + " % discount!";
                displayMessage(promoMessage, Toast.LENGTH_SHORT);
            }
            if(numberLeft <= 0 && !promoEnabled){
                promoMessage = "A " + promoDiscount + " % discount will be subtracted from your order total.";
                displayMessage(promoMessage, Toast.LENGTH_LONG);
                promoEnabled = true;
            }
        }
        Button decButton = findViewById(R.id.dec_button);
        decButton.setEnabled(true);
        display(numberOfCoffees);
    }
    public void decrementOrder(View view){
        clearFocusNameField();
        Button decButton = findViewById(R.id.dec_button);
        String promoMessage;
        if (numberOfCoffees > 0){
            if (numberOfCoffees == 10 && promoEnabled){
                promoMessage = "Your " + promoDiscount + " % discount is  now cancelled.";
                displayMessage(promoMessage, Toast.LENGTH_LONG);
                promoEnabled = false;
            }
            numberOfCoffees -= 1;
        }
        if(numberOfCoffees < 1){
            decButton.setEnabled(false);
            numberOfCoffees=0;
        }
        display(numberOfCoffees);
    }
    public void clearFocusNameField(){
        EditText editText = findViewById(R.id.userNameField);
        if (editText.hasFocus()) {
            editText.clearFocus();
        }
    }
    private void display(int number){
        TextView quantityTextView = findViewById(R.id.quantity_text_view);
        CheckBox chkOpt1 = findViewById(R.id.chk_opt1);
        CheckBox chkOpt2 = findViewById(R.id.chk_opt2);
        String moneySymbol = getString(R.string.sym_val);
        String numberString = ""+number;
        hasOpt1 = chkOpt1.isChecked();
        hasOpt2 = chkOpt2.isChecked();
        if(number == 0){
            totalPrice = 0.00;
            amountDue = moneySymbol + totalPrice;
        }else {
            totalPrice = 0.00;
            if(hasOpt1){
                totalPrice += (double) number * opt1Price;
            }
            if(hasOpt2){
                totalPrice += (double) number * opt2Price;
            }
            if (promoEnabled){
                double discountAmount;
                totalPrice += ((double) number) * coffeePrice;
                discountAmount = totalPrice / 100 * promoDiscount;
                totalPrice = totalPrice - discountAmount;
                totalPrice = round(totalPrice,2);
                amountDue = moneySymbol + totalPrice + "\n" + getString(R.string.disc_txt) + " " + promoDiscount + "%.";
            }else{
                totalPrice += ((double) number) * coffeePrice;
                totalPrice = round(totalPrice,2);
                amountDue = moneySymbol + totalPrice;
            }
        }
        quantityTextView.setText(numberString);
        createOrderSummary();
    }
    public void updateCheckBox(View view){
        clearFocusNameField();
        display(numberOfCoffees);
    }
    private void displayMessage(String message, int duration){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
    private void createOrderSummary(){
        String opt1YN = "No";
        String opt2YN = "No";
        if(hasOpt1){
            opt1YN= "Yes";
        }
        if(hasOpt2){
            opt2YN= "Yes";
        }
        TextView orderSummaryTextView = findViewById(R.id.order_summary_text_view);
        EditText userNameField = findViewById(R.id.userNameField);
        userName = userNameField.getText().toString();
        orderSummary = "Name: "+ userName +" \nQuantity: "+ numberOfCoffees +"\n"+ getString(R.string.opt_1) +": "+ opt1YN +"\n"+ getString(R.string.opt_2) +": "+ opt2YN +"\nTotal: " + amountDue + "\nThank you!";
        orderSummaryTextView.setText(amountDue);
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public void submitOrder(View view){
        //submit the order
        String[] orderAddress ={getString(R.string.order_mail)};
        String orderSubject = getString(R.string.order_subj) + " " + userName;
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL,orderAddress);
        i.putExtra(Intent.EXTRA_SUBJECT,orderSubject);
        i.putExtra(Intent.EXTRA_TEXT,orderSummary);
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
        }
    }
}
