package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText eTName, eTMin, eTMax, eTAge;
    private Spinner spinner;
    private Button btnSend, btnUpdate;
    private ListView listView;

    private GraDataBase graDataBase;
    private List<Gra> gryList;
    private ArrayAdapter<Gra> arrayAdapter;
    private Gra kliknietaGra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ini();

        graDataBase = Room.databaseBuilder(
                        getApplicationContext(), GraDataBase.class, "gra_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        gryList = graDataBase.zwrocGryDAO().zwrocWszystkieGry();
        arrayAdapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                gryList
        );
        listView.setAdapter(arrayAdapter);

        btnSend.setOnClickListener(view -> {
            if (!poprawneDane()) return;
            Gra gra = new Gra(
                    eTName.getText().toString().trim(),
                    Integer.parseInt(eTMin.getText().toString().trim()),
                    Integer.parseInt(eTMax.getText().toString().trim()),
                    Integer.parseInt(eTAge.getText().toString().trim()),
                    spinner.getSelectedItem().toString()
            );
            graDataBase.zwrocGryDAO().wstawGreDoBazy(gra);
            gryList.clear();
            gryList.addAll(graDataBase.zwrocGryDAO().zwrocWszystkieGry());
            arrayAdapter.notifyDataSetChanged();
            wyczysc();
        });

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Gra doUsuniecia = gryList.get(i);
            graDataBase.zwrocGryDAO().usunZBazy(doUsuniecia);
            gryList.remove(i);
            arrayAdapter.notifyDataSetChanged();
            if (doUsuniecia == kliknietaGra) {
                kliknietaGra = null;
                btnUpdate.setVisibility(View.INVISIBLE);
                wyczysc();
            }
            return true;
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            kliknietaGra = gryList.get(i);

            eTName.setText(kliknietaGra.nazwa);
            eTMin.setText("" + kliknietaGra.minLiczbaOsob);
            eTMax.setText("" + kliknietaGra.maksLiczbaOsob);
            eTAge.setText("" + kliknietaGra.kategoriaWiekowa);

            for (int j = 0; j < spinner.getCount(); j++) {
                if (spinner.getItemAtPosition(j).toString().equals(kliknietaGra.kategoriaNazwa)) {
                    spinner.setSelection(j);
                    break;
                }
            }

            btnUpdate.setVisibility(View.VISIBLE);
        });

        btnUpdate.setOnClickListener(v -> {
            if (kliknietaGra == null || !poprawneDane()) return;
            kliknietaGra.nazwa = eTName.getText().toString().trim();
            kliknietaGra.minLiczbaOsob = Integer.parseInt(eTMin.getText().toString().trim());
            kliknietaGra.maksLiczbaOsob = Integer.parseInt(eTMax.getText().toString().trim());
            kliknietaGra.kategoriaWiekowa = Integer.parseInt(eTAge.getText().toString().trim());
            kliknietaGra.kategoriaNazwa = spinner.getSelectedItem().toString();

            graDataBase.zwrocGryDAO().aktualizujGre(kliknietaGra);
            arrayAdapter.notifyDataSetChanged();

            kliknietaGra = null;
            btnUpdate.setVisibility(View.INVISIBLE);
            wyczysc();
        });
    }

    private boolean poprawneDane() {
        String nazwa = eTName.getText().toString().trim();
        String min = eTMin.getText().toString().trim();
        String max = eTMax.getText().toString().trim();
        String wiek = eTAge.getText().toString().trim();

        if (nazwa.isEmpty()) {
            eTName.setError("Podaj nazwę");
            return false;
        }
        if (min.isEmpty()) {
            eTMin.setError("Podaj liczbę");
            return false;
        }
        if (max.isEmpty()) {
            eTMax.setError("Podaj liczbę");
            return false;
        }
        if (wiek.isEmpty()) {
            eTAge.setError("Podaj wiek");
            return false;
        }
        if (min.length() > 4 || max.length() > 4 || wiek.length() > 4) {
            eTMin.setError("Za duża liczba");
            return false;
        }

        int minInt = Integer.parseInt(min);
        int maxInt = Integer.parseInt(max);

        if (minInt < 1) {
            eTMin.setError("Minimum to 1");
            return false;
        }
        if (minInt > maxInt) {
            eTMax.setError("Max musi być większe lub równe min");
            return false;
        }
        return true;
    }

    private void wyczysc() {
        eTName.setText("");
        eTMin.setText("");
        eTMax.setText("");
        eTAge.setText("");
    }

    private void ini() {
        eTName = findViewById(R.id.eTName);
        eTMin = findViewById(R.id.eTMin);
        eTMax = findViewById(R.id.eTMax);
        eTAge = findViewById(R.id.eTAge);
        spinner = findViewById(R.id.spinnerCategory);
        btnSend = findViewById(R.id.btnWyslij);
        btnUpdate = findViewById(R.id.btnUpdate);
        listView = findViewById(R.id.listView);
    }
}