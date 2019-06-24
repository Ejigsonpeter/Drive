package drive.ejigapeter.com.drive;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class listOnlineViewHolder  extends RecyclerView.ViewHolder {
    public TextView txtEmail;
    public  listOnlineViewHolder(View itemView){
        super(itemView);
        txtEmail = (TextView)itemView.findViewById(R.id.txt_email);


    }
}
