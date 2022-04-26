package com.example.sns_project.SignLogins;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sns_project.BasicActivity;
import com.example.sns_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.regex.Pattern;

public class SearchIDActivity extends BasicActivity {
    private static final String TAG = "SearchIDActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_id);

        findViewById(R.id.checkIDButton).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            if(id == R.id.checkIDButton){

                String number = ((EditText) findViewById(R.id.searchID)).getText().toString();

                if(number != null){
                    if(number.length() == 11){
                        if(Pattern.matches("^[0-9]*$",number)){
                            searchId(number);
                        }else{
                            startToast("숫자만 입력해주세요(-제외)");
                        }
                    }else{
                        startToast("전화번호는 11자리 숫자입니다(-제외)");
                    }
                }else{
                    startToast("전화번호를 입력해주세요");
                }

            }
        }
    };



    private void searchId(String number){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean flag = true;

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "find_id_log " + document.getId() + " => " + document.getData() + " userEmail" );

                                Map<String, Object> userData = document.getData();
                                String userNumber = userData.get("phoneNumber").toString();
                                if(userNumber.equals(number)){
                                    String userEmail =  userData.get("email").toString();
                                    sendSMS(userEmail, userNumber);
                                    flag = false;
                                    break;
                                }
                            }
                            if(flag){
                                startToast("해당 번호가 존재하지 않습니다");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void startToast(String msg){
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }

    private void sendSMS(String email, String number){

        View dialogView = (View) View.inflate(SearchIDActivity.this,R.layout.dialog_search_id,null);
        ProgressBar timerBar = (ProgressBar) dialogView.findViewById(R.id.timerBar);
        TextView timerNumber = (TextView) dialogView.findViewById(R.id.timerNumber);

        AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                .setTitle("코드 입력")
                .setView(dialogView)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startToast("취소했습니다");
                    }
                });

        AlertDialog aDialog = dlg.create();

        int randomCode = (int) (1 + Math.random() * 999999);
        Log.d(TAG, "codeNumber1 "+ randomCode);
        String codeQ = String.valueOf(randomCode);
        startToast(codeQ);

        EditText codeText = (EditText) dialogView.findViewById(R.id.inputCode);
        Button codeBtn = (Button) dialogView.findViewById(R.id.checkCodeBtn);

        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String codeA = codeText.getText().toString();
                String codeQ = String.valueOf(randomCode);

                if(codeA.equals(codeQ)){
                    aDialog.cancel();
                    conFirmID(email);


                }else{
                    startToast("코드가 다릅니다");
                }
            }
        });


        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = timerBar.getProgress() ; i > 0 ; i--) {
                    timerBar.setProgress(timerBar.getProgress() - 1);
                    timerNumber.setText("남은 시간 : " + timerBar.getProgress());
                    SystemClock.sleep(1000);
                }
                SearchIDActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SearchIDActivity.this, "시간이 초과되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                aDialog.cancel();
            }
        });

        th.setDaemon(true);
        th.start();

        aDialog.show();
    }




    private void conFirmID(String email){

        AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                .setTitle("아이디 확인")
                .setMessage(email)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startToast("확인되었습니다");
                        finish();
                    }
                });

        AlertDialog alertDialog = dlg.show();
        TextView messageText = (TextView)alertDialog.findViewById(android.R.id.message);
        messageText.setTextSize(25);
        alertDialog.show();

    }


}
