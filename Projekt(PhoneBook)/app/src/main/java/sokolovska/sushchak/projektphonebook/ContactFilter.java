package sokolovska.sushchak.projektphonebook;


import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import sokolovska.sushchak.projektphonebook.adapters.SimpleAdapter;


public class ContactFilter extends Filter {

    private List<Contact> contactList;
    private List<Contact> filteredContactList;
    private SimpleAdapter adapter;

    public ContactFilter(List<Contact> contactList, SimpleAdapter adapter) {
        this.contactList = contactList;
        this.adapter = adapter;
        this.filteredContactList = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredContactList.clear();
        final FilterResults results = new FilterResults();

        for (final Contact item : contactList){
            String text = item.getName() + item.getSecondName();
            if(text.toLowerCase().trim().contains(constraint)){
                filteredContactList.add(item);
            }

        }
        results.values = filteredContactList;
        results.count = filteredContactList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.setList(filteredContactList);
        adapter.notifyDataSetChanged();
    }
}
