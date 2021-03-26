package com.example.appqlttcanhan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final static int requestcode = 1113;
    final static int resultcodeadd = 1114;
    final static int resultcodeupdate = 1115;
    Button btnthoatapp,btnthemperson,btnloc;
    Spinner spquequan;
    ListView lvthongtin;
    int indexselect;
    String[] arrdsquequan;
    String quequan,idselect;
    ArrayList<Person> listPerson = new ArrayList<>();
    ArrayList<Person> listtheoquequan = new ArrayList<>();
    listview_Custom_Adapter adapter_custom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taoDanhSachTT();
        mapping();
        listtheoquequan.addAll(listPerson);
        adapter_custom = new listview_Custom_Adapter(getApplicationContext(),R.layout.dong_thong_tin_person,listtheoquequan);
        lvthongtin.setAdapter(adapter_custom);
        btnthoatapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        arrdsquequan = getResources().getStringArray(R.array.Danhsachquequan);
        btnthemperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Layout_them.class);
                String id = listPerson.get(listPerson.size()-1).getId().toString();
                intent.putExtra("idmax",id);
                startActivityForResult(intent,requestcode);
            }
        });
        spinner_Custom_Adapter adapter = new spinner_Custom_Adapter(getApplicationContext(),R.layout.spinner_que_quan,arrdsquequan);
        spquequan.setAdapter(adapter);
        spquequan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                quequan = arrdsquequan[position].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        registerForContextMenu(lvthongtin);
        btnloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getlisttheoquequan(listtheoquequan,quequan);
                adapter_custom = new listview_Custom_Adapter(v.getContext(),R.layout.dong_thong_tin_person,listtheoquequan);
                lvthongtin.setAdapter(adapter_custom);
            }
        });

        lvthongtin.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, listtheoquequan.get(position).getName(), Toast.LENGTH_SHORT).show();
                idselect = listtheoquequan.get(position).getId().toString();
                indexselect = position;
                return false;
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_quanly,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_sua:
                Intent suaintent = new Intent(getApplicationContext(),Layout_sua.class);
                Bundle bundle = new Bundle();
                Person personmodify = listtheoquequan.get(indexselect);
                bundle.putSerializable("personmodify",personmodify);
                suaintent.putExtra("thongtinchinhsua",bundle);
                startActivityForResult(suaintent,requestcode);
                Toast.makeText(this, "Update is Open", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_xoa:
                Xacnhanxoa(idselect);
                break;
        }
        return super.onContextItemSelected(item);
    }
    public void Xacnhanxoa(String id)
    {
        AlertDialog.Builder alertxoa = new AlertDialog.Builder(this);
        alertxoa.setMessage("Xác nhận xóa");
        alertxoa.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (id != null)
                {
                    for (int i = 0;i<listPerson.size();i++)
                    {
                        if(listPerson.get(i).getId().equals(id))
                        {
                            listPerson.remove(i);
                        }
                    }
                    for (int i = 0;i<listtheoquequan.size();i++)
                    {
                        if(listtheoquequan.get(i).getId().equals(id))
                        {
                            listtheoquequan.remove(i);
                        }
                    }
                    adapter_custom.notifyDataSetChanged();
                }

            }});
        alertxoa.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertxoa.show();
    }


    public void mapping()
    {
        btnthoatapp = (Button) findViewById(R.id.buttonThoat);
        btnthemperson = (Button) findViewById(R.id.buttonThem);
        btnloc = (Button) findViewById(R.id.buttonLoc);
        spquequan = (Spinner) findViewById(R.id.spinnerQuequan);
        lvthongtin = (ListView) findViewById(R.id.listviewThongtin);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestcode) {
            Bundle bundle;
            if (resultCode == resultcodeadd && data != null) {
                bundle = data.getBundleExtra("resultFromAdd");
                Person newperson = (Person)bundle.getSerializable("newperson");
                listPerson.add(newperson);
                listtheoquequan.clear();
                listtheoquequan.addAll(listPerson);
            }
            if(resultCode == resultcodeupdate && data != null)
            {
                bundle = data.getBundleExtra("resultFromUpdate");
                Person updateperson = (Person) bundle.getSerializable("personAfterModify");
                for(int i = 0;i<listPerson.size();i++)
                {
                    if(listPerson.get(i).getId().equals(updateperson.getId()))
                    {
                        listPerson.set(i,updateperson);
                    }
                }
                listtheoquequan.clear();
                listtheoquequan.addAll(listPerson);
                Toast.makeText(this, "da update", Toast.LENGTH_SHORT).show();
            }
            adapter_custom.notifyDataSetChanged();
        }
    }

    public void taoDanhSachTT()
    {

        listPerson.add(new Person("1","Trần Minh Quân","0868655861","453/54 Lê Hồng Phong","Bình Phước"));
        listPerson.add(new Person("2","Nguyễn Hoài Nam","0848723439","Thích Quảng Đức","Đắk Lắk"));
    }
    public void getlisttheoquequan(ArrayList<Person> listPersonquequan,String quequan)
    {
        listPersonquequan.clear();
        if(quequan.equals("Tất cả"))
        {
            for(int i = 0;i<listPerson.size();i++)
            {
                listPersonquequan.add(listPerson.get(i));
            }
        }
        else
        {
            for(int i = 0;i<listPerson.size();i++)
            {
                if(listPerson.get(i).getCountry().toString().equals(quequan))
                {
                    listPersonquequan.add(listPerson.get(i));
                }
            }
        }
    }
}