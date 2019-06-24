package drive.ejigapeter.com.drive;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;

import drive.ejigapeter.com.drive.model.user;

public class listOnline extends AppCompatActivity {
    DatabaseReference onlineRef,currentUserRef,counterRef;
    FirebaseRecyclerAdapter <user,listOnlineViewHolder> adapter;

    RecyclerView listonline;
    RecyclerView.LayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);
//initialize the view
        listonline = (RecyclerView)findViewById(R.id.listonline);
        listonline.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(this);
        listonline.setLayoutManager(layoutManager);
//set toolbar and logout menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Equiping Tracking System");
        setSupportActionBar(toolbar);

        //online

        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("lastOnline");
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);

        return true;
    }


}
