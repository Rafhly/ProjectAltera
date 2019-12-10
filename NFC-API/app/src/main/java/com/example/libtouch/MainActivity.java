package com.example.libtouch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.libtouch.API.LibraryAPI;

import org.apache.xmlrpc.XmlRpcException;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    String url;
    String db;
    String username;
    String password;
    EditText studentIDEditText;
    EditText bookIDEditText;
    TextView displayTextView;
    int[] uid = new int[1];
    String[] borrowInfo = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button borrowBtn = (Button) findViewById(R.id.borrowBtn);
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch latch1 = new CountDownLatch(1);

        borrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentIDEditText = (EditText) findViewById(R.id.studentIDEditText);
                bookIDEditText = (EditText) findViewById(R.id.bookIDEditText);
                displayTextView = (TextView) findViewById(R.id.displayTextView);

                uid[0] = 0;

                final String studentID = studentIDEditText.getText().toString();
                final String bookID = bookIDEditText.getText().toString();

                url = "http://192.168.254.118:8069";
                db = "AdrianovTest";
                username = "adrianovdelta@gmail.com";
                password = "iloveanimetiddies";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String borrowtimestamp = sdf.format(date).toString();

                Thread logginIn = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LibraryAPI libraryAPI = new LibraryAPI();

                        try {
                            uid[0] = libraryAPI.loggingIn(url, db, username, password);
                            latch.countDown();
                        } catch (MalformedURLException e) {
                            uid[0] = 420;
                            latch.countDown();
                            e.printStackTrace();
                        } catch (XmlRpcException e) {
                            uid[0] = 666;
                            latch.countDown();
                            e.printStackTrace();
                        }
                    }
                });

                logginIn.start();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Thread reqBorrowInfo = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LibraryAPI libraryAPI = new LibraryAPI();

                        borrowInfo = libraryAPI.BorrowRequestInfo(url, studentID, bookID, db, uid[0], password);
                        latch1.countDown();

                    }
                });

                reqBorrowInfo.start();
                try {
                    latch1.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//
                if(borrowInfo[0] == "null" && borrowInfo[1] == "null") {
                    displayTextView.setText("You are not registered as a student/faculty member! \n" +
                            " The book you are trying to borrow isn't registered in the database");
                }
                else if(borrowInfo[0] == "null") {
                    displayTextView.setText("You are not registered as a student/faculty member!");
                }
                else if(borrowInfo[1] == "null") {
                    displayTextView.setText("The book you are trying to borrow isn't registered in the database");
                }
                else {
                    displayTextView.setText(borrowInfo[0] + " has successfully borrowed " + borrowInfo[1] + "at \n" +
                            borrowtimestamp);
                }
            }
        });

    }
}
