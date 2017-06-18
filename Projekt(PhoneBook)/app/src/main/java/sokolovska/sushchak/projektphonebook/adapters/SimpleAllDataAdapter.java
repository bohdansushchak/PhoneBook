package sokolovska.sushchak.projektphonebook.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import sokolovska.sushchak.projektphonebook.Communication;
import sokolovska.sushchak.projektphonebook.Contact;
import sokolovska.sushchak.projektphonebook.R;


public class SimpleAllDataAdapter extends RecyclerView.Adapter<SimpleAllDataAdapter.ViewHolder> {

    private Context context;
    private int[] idIcons;
    private ArrayList<String> data;
    private ArrayList<String> title;
    private Contact contact;

    private Communication communication;

    public SimpleAllDataAdapter(Context context, int[] idIcons, ArrayList<String> data, ArrayList<String> title, Contact contact) {

        if (context == null)
            throw new IllegalArgumentException("Context cannot be null");
        this.context = context;

        communication = new Communication(context);

        this.idIcons = idIcons;
        this.data = data;
        this.title = title;

        this.contact = contact;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.data.setText(data.get(position));
        holder.title.setText(title.get(position));
        holder.icon.setImageResource(idIcons[position]);

        if(title.get(position).equals(context.getString(R.string.add_mobile))
                || title.get(position).equals(context.getString(R.string.add_workMobile)))

            holder.iBtnSendSMS.setVisibility(View.VISIBLE);
        else holder.iBtnSendSMS.setVisibility(View.INVISIBLE);

        holder.iBtnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communication.sendSMS(data.get(position));
            }
        });

        holder.data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(title.get(position).equals(context.getString(R.string.add_mobile))
                        || title.get(position).equals(context.getString(R.string.add_workMobile))) {
                    communication.call(data.get(position));
                }

                if (holder.title.getText().equals(context.getString(R.string.add_mail))
                        || holder.title.getText().equals(context.getString(R.string.add_workMail))){
                    communication.sendEmail(data.get(position));
                }

                if (holder.title.getText().equals(context.getString(R.string.add_address))
                        || holder.title.getText().equals(context.getString(R.string.add_workAddress))){
                    communication.viewAddressOnMap(data.get(position));
                }
                if (holder.title.getText().equals(context.getString(R.string.add_happyBirthday))){

                    StringBuilder contactName = new StringBuilder();
                    if(contact.getName() != null) {
                        contactName.append(contact.getName());
                        contactName.append(" ");
                    }
                    if(contact.getSecondName() != null) {

                        contactName.append(contact.getSecondName());
                    }
                    communication.addDateIntoCalendar(data.get(position), contactName.toString());
                }
            }
        });

        holder.data.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(context, "Copy into buffer", Toast.LENGTH_SHORT).show();
                //TODO доробити збереження в буфер
                return true;
            }
        });

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView icon;
        private TextView data;
        private TextView title;
        private ImageButton iBtnSendSMS;

        public ViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            data = (TextView) itemView.findViewById(R.id.tv_data);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            iBtnSendSMS = (ImageButton) itemView.findViewById(R.id.i_btn);
        }
    }
}
