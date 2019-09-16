package KumaTraining13.Bear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BearMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bear_menu);

        Button btn = findViewById(R.id.btn_lat1);
        Button btn2 = findViewById(R.id.btn_lat2);
        initIntent(btn,Beartivity.class);
        initIntent(btn2,Beartivity2.class);
    }

    private void initIntent(Button btn,final Class bearclass){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bearsense = new Intent(BearMenu.this,bearclass);
                startActivity(bearsense);
            }
        });
    }
}
