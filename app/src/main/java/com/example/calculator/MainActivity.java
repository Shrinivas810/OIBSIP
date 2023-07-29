package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    // Views
    private TextView resultTextView;
    private StringBuilder inputString = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        resultTextView = findViewById(R.id.resultTextView);

        // Set click listeners for numeric buttons
        setNumericClickListener(R.id.btnZero);
        setNumericClickListener(R.id.btnOne);
        setNumericClickListener(R.id.btnTwo);
        setNumericClickListener(R.id.btnThree);
        setNumericClickListener(R.id.btnFour);
        setNumericClickListener(R.id.btnFive);
        setNumericClickListener(R.id.btnSix);
        setNumericClickListener(R.id.btnSeven);
        setNumericClickListener(R.id.btnEight);
        setNumericClickListener(R.id.btnNine);

        // Set click listeners for other buttons
        findViewById(R.id.btnAdd).setOnClickListener(operationClickListener);
        findViewById(R.id.btnSubtract).setOnClickListener(operationClickListener);
        findViewById(R.id.btnMultiply).setOnClickListener(operationClickListener);
        findViewById(R.id.btnDivide).setOnClickListener(operationClickListener);
        findViewById(R.id.btnModulo).setOnClickListener(operationClickListener);

        findViewById(R.id.btnDecimal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputString.length() == 0) {
                    inputString.append("0.");
                } else if (!isOperator(inputString.charAt(inputString.length() - 1))) {
                    inputString.append(".");
                }
                updateResult();
            }
        });

        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateResult();
            }
        });

        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputString.setLength(0);
                updateResult();
            }
        });


        findViewById(R.id.btnBackspace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputString.length() > 0) {
                    inputString.deleteCharAt(inputString.length() - 1);
                    updateResult();
                }
            }
        });
        // Find the Close App button by its ID
        Button btnCloseApp = findViewById(R.id.btnCloseApp);
        btnCloseApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the activity to close the app
                finish();
            }
        });}

    private void setNumericClickListener(int buttonId) {
        findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                inputString.append(button.getText().toString());
                updateResult();
            }
        });
    }

    private View.OnClickListener operationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button button = (Button) view;
            String buttonText = button.getText().toString();

            if (inputString.length() > 0 && !isOperator(inputString.charAt(inputString.length() - 1))) {
                inputString.append(buttonText);
                updateResult();
            }
        }
    };

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private void updateResult() {
        resultTextView.setText(inputString.toString());
    }

    private void calculateResult() {
        try {
            String result = evaluateExpression(inputString.toString());
            inputString.setLength(0);
            inputString.append(result);
            updateResult();
        } catch (Exception e) {
            inputString.setLength(0);
            inputString.append("Error");
            updateResult();
        }
    }

    private String evaluateExpression(String expression) {
        try {
            double result = evaluateExpressionHelper(expression);
            return new DecimalFormat("#.#####").format(result);
        } catch (Exception e) {
            return "Error";
        }
    }

    private double evaluateExpressionHelper(String expression) {
        char[] exprChars = expression.toCharArray();
        int length = expression.length();

        // Find the first operator with lowest precedence (from left to right)
        int operatorIndex = -1;
        int bracketCount = 0;
        int minPrecedence = Integer.MAX_VALUE;

        for (int i = 0; i < length; i++) {
            char c = exprChars[i];

            if (c == '(') {
                bracketCount++;
            } else if (c == ')') {
                bracketCount--;
            } else if (bracketCount == 0 && isOperator(c)) {
                int precedence = getPrecedence(c);
                if (precedence <= minPrecedence) {
                    minPrecedence = precedence;
                    operatorIndex = i;
                }
            }
        }

        if (operatorIndex != -1) {
            // Operator found, split the expression into left and right parts
            String left = expression.substring(0, operatorIndex).trim();
            String right = expression.substring(operatorIndex + 1).trim();
            char operator = exprChars[operatorIndex];

            double leftValue = evaluateExpressionHelper(left);
            double rightValue = evaluateExpressionHelper(right);

            // Perform the operation based on the operator
            switch (operator) {
                case '+':
                    return leftValue + rightValue;
                case '-':
                    return leftValue - rightValue;
                case '*':
                    return leftValue * rightValue;
                case '/':
                    if (rightValue == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    return leftValue / rightValue;
                case '%':
                    return leftValue % rightValue;
            }
        }


        return Double.parseDouble(expression);
    }

    private int getPrecedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '%':
                return 2;
            default:
                return 0;
        }
    }
}
