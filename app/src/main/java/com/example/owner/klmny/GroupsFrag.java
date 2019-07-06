package com.example.owner.klmny;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFrag extends Fragment {

    private View groupView;
    private ListView listView;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private DatabaseReference reference;
    
    public GroupsFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupView =  inflater.inflate(R.layout.fragment_groups, container, false);
        reference = FirebaseDatabase.getInstance().getReference().child("Groups");

        IntializeFields();
        getTheGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Ilname = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(getContext(), GroupChat.class);
                intent.putExtra("namer", Ilname);
                startActivity(intent);
            }
        });
        return groupView;
    }

    private void IntializeFields() {
        listView = (ListView) groupView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);
    }

    private void getTheGroups(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                list.clear();
                list.addAll(set);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
