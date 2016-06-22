package cc.solart.turbo.simple;

import android.content.ContentUris;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void simple(View viw){
        Intent intent = new Intent(this,SimpleActivity.class);
        startActivity(intent);
    }

    public void multi(View viw){
        Intent intent = new Intent(this,MultiTypeActivity.class);
        startActivity(intent);
    }

    public void empty(View viw){
        Intent intent = new Intent(this,EmptyActivity.class);
        startActivity(intent);
    }

    public void cursor(View viw){
        Intent intent = new Intent(this,CursorActivity.class);
        startActivity(intent);
    }
}
