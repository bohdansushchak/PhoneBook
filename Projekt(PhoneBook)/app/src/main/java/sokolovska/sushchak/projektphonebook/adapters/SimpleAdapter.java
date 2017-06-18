package sokolovska.sushchak.projektphonebook.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import sokolovska.sushchak.projektphonebook.Communication;
import sokolovska.sushchak.projektphonebook.Contact;
import sokolovska.sushchak.projektphonebook.ContactFilter;

import sokolovska.sushchak.projektphonebook.DBHelper;
import sokolovska.sushchak.projektphonebook.R;
import sokolovska.sushchak.projektphonebook.activities.AddNewContactActivity;
import sokolovska.sushchak.projektphonebook.activities.FullDataContactActivity;


public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contacts;

    private ContactFilter filter;
    private int idItem;
    private int idTvName;
    private int idTvSecondName;
    private int idIvPhoto;


    public SimpleAdapter(Context context, List contacts, int idItem, int idTvName, int idTvSecondName, int idIvPhoto) {
        if (context == null)
            throw new IllegalArgumentException("Context cannot be null");

        this.context = context;

        if(contacts == null)
            throw new IllegalArgumentException("Contacts cannot be null");

        this.contacts = contacts;

        this.idItem = idItem;
        this.idIvPhoto = idIvPhoto;
        this.idTvName = idTvName;
        this.idTvSecondName = idTvSecondName;

        filter = new ContactFilter(contacts, this);


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(idItem, parent, false );
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

            if (contacts.get(position).getPhoto() == null) {
                holder.photo.setImageResource(R.drawable.person);
            } else if (!new File(contacts.get(position).getPhoto()).exists()) {
                holder.photo.setImageResource(R.drawable.person);
            } else {
                Glide.with(context)
                        .load(contacts.get(position).getPhoto())
                        .into(holder.photo);
            }

        holder.name.setText(contacts.get(position).getName());
        holder.secondName.setText(contacts.get(position).getSecondName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullDataContactActivity.class);
                intent.putExtra("ContactId", contacts.get(position).getId());
                Toast.makeText(context, "contact id" + contacts.get(position).getId(), Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                PopupMenu popupMenu = new PopupMenu(context, holder.itemView);
                popupMenu.inflate(R.menu.context_menu_all_contacts);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.context_menu_open:
                                Intent intentView = new Intent(context, FullDataContactActivity.class);
                                intentView.putExtra("ContactId", contacts.get(position).getId());
                                context.startActivity(intentView);
                                break;

                            case R.id.context_menu_share:
                                Cursor cursor = new DBHelper(context).getContact(contacts.get(position).getId());
                                Contact contact = new Contact(context, cursor);
                                String data = contact.allData();
                                Communication communication = new Communication(context);
                                communication.sendEmail("", data);
                                break;

                            case R.id.context_menu_edit:
                                Intent intentEdit = new Intent(context, AddNewContactActivity.class);
                                intentEdit.putExtra("Edit", true);
                                intentEdit.putExtra("ContactId", contacts.get(position).getId());
                                context.startActivity(intentEdit);
                                break;

                            case R.id.context_menu_delete:
                                new DBHelper(context).deleteContact(contacts.get(position).getId());
                                contacts.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Contact usunięto", Toast.LENGTH_SHORT).show();
                                break;
                        }

                        return true;
                    }


                });
                popupMenu.show();

                return true;
            }
        });

    }



    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setList(List<Contact> list){
        this.contacts = list;
    }

    public void filterList(String text){
        filter.filter(text);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView photo;
        private TextView name;
        private TextView secondName;

        public ViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(idIvPhoto); //R.id.iv_image_item
            name = (TextView) itemView.findViewById(idTvName); //R.id.tv_list_name
            secondName = (TextView) itemView.findViewById(idTvSecondName); // R.id.tv_list_second_name
        }


    }



}
