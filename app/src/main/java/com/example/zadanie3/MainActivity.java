package com.example.zadanie3;

//2020 Karol Buchajczuk
//calslock@github


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    DrawHandle drawHandle;          //Klasa odpowiedzialna za rysowanie
    ConstraintLayout mainLayout;    //Główny layout
    LinearLayout buttonLayout;      //LinearLayout, zawierający przyciski
    String customColor;             //String przechowujący własny kolor
    //Tablica z przyciskami - 6 kolorów, kolor własny, czyszczenie, zwiększenie i zmniejszenie pędzla
    Button[] buttons = new Button[10];

    //Builder AlertDialog
    //Służy do pokazywania okna z możliwością wyboru własnego koloru
    AlertDialog.Builder builder;
    //Input - pole, w którym wpisywana jest wartość własnego koloru
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = new EditText(this);
        builder = new AlertDialog.Builder(this);

        mainLayout = findViewById(R.id.main);
        drawHandle = new DrawHandle(this);
        mainLayout.addView(drawHandle);

        initButtons();
    }

    //Metoda inicjalizująca i pokazująca przyciski
    void initButtons(){
        buttonLayout = new LinearLayout(this);
        //Tutaj -1 == MATCH_PARENT
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1, 1);

        //Pętla ustawiająca właściwości przycisków
        for(int i=0; i<buttons.length; i++){
            //Dodanie przycisków do tablicy
            buttons[i] = new Button(this);
            //Ustawienie parametrów przycisków
            buttons[i].setLayoutParams(params);
            switch(i){
                case 0: buttons[i].setBackgroundColor(Color.RED);   //Dla każdego przycisku ustaw kolor tła
                        buttons[i].setOnClickListener(new View.OnClickListener(){   //I listener przy wciśnięciu
                            @Override
                            public void onClick(View v){
                                drawHandle.setColor("#ff0000");     //Który docelowo zmieni kolor
                            }
                        });
                        break;
                case 1: buttons[i].setBackgroundColor(Color.rgb(255,127,0));
                        buttons[i].setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                drawHandle.setColor("#ff7f00");
                            }
                        });
                break;
                case 2: buttons[i].setBackgroundColor(Color.YELLOW);
                        buttons[i].setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                drawHandle.setColor("#ffff00");
                            }
                        });
                break;
                case 3: buttons[i].setBackgroundColor(Color.GREEN);
                        buttons[i].setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                drawHandle.setColor("#00ff00");
                            }
                        });
                break;
                case 4: buttons[i].setBackgroundColor(Color.BLUE);
                        buttons[i].setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                drawHandle.setColor("#0000ff");
                            }
                        });
                break;
                case 5: buttons[i].setBackgroundColor(Color.MAGENTA);
                        buttons[i].setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                drawHandle.setColor("#ff00ff");
                            }
                        });
                break;
                //Własny kolor
                case 6: buttons[i].setText("C");
                    buttons[i].setBackgroundColor(Color.WHITE);
                        buttons[i].setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                //Przy wcisnięciu inicjalizowany jest builder
                                builder.setTitle("Wpisz kolor (hex):");
                                //Ustawiane są filtry dla EditTexta - max. 6 znaków i tylko wartości szesnastkowe
                                input.setFilters(new InputFilter[]{
                                        new InputFilter.LengthFilter(6), filterHex}
                                );
                                //Oraz wyłączenie podpowiedzi z klawiatury
                                input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                                //Dodanie EditText do buildera
                                builder.setView(input);

                                //W przypadku wciśnięcia OK
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Sprawdzana jest długość wejścia
                                        if(input.getText().toString().length() != 6)    showToast("Zła wartość (6 znaków)");
                                        //Jeżeli się zgadza to ustawiany jest dany kolor
                                        else{
                                            customColor = "#";
                                            customColor += input.getText().toString();
                                            drawHandle.setColor(customColor);
                                        }
                                    }
                                });

                                //W przypadku wciśnięcia Anuluj
                                builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Zamknięcie okienka
                                        dialog.cancel();
                                    }
                                });
                                //Jeżeli jest już ustawiony EditText View, usuń duplikaty
                                if(input.getParent() != null) ((ViewGroup)input.getParent()).removeView(input);
                                //Wyświetl okienko
                                builder.show();
                            }
                        });
                break;
                //Czyszczenie ekranu
                case 7: buttons[i].setText("X");
                        buttons[i].setBackgroundColor(Color.WHITE);
                        buttons[i].setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                drawHandle.clearView();
                            }
                        });
                break;
                //Zwiększenie rozmiaru pędzla
                case 8: buttons[i].setText("+");
                        buttons[i].setBackgroundColor(Color.WHITE);
                        buttons[i].setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                int size = drawHandle.changeStrokeWidth(DrawHandle.INCREASE);
                                showToast("Rozmiar pędzla: "+size);
                            }
                        });
                break;
                //Zmniejszenie rozmiaru pędzla
                case 9: buttons[i].setText("-");
                        buttons[i].setBackgroundColor(Color.WHITE);
                        buttons[i].setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                int size = drawHandle.changeStrokeWidth(DrawHandle.DECREASE);
                                showToast("Rozmiar pędzla: "+size);
                            }
                        });
                break;
            }   //koniec switcha
            buttonLayout.addView(buttons[i]);
        }   //koniec for button[i]
        mainLayout.addView(buttonLayout);
    }   //koniec initButtons()

    //Filter do EditText, przyjmuje tylko wartości hex
    InputFilter filterHex = new InputFilter(){
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
            // POSIX \p{XDigit} => 0-9A-Fa-f, tylko wartości szesnastkowe
            Pattern pattern = Pattern.compile("^\\p{XDigit}+$");
            StringBuilder sb = new StringBuilder();
            for(int i=start; i<end; i++){
                //jeżeli nie jest literą lub cyfrą - nie dodawaj do końcowego stringa
                if (!Character.isLetterOrDigit(source.charAt(i))){
                    return "";
                }
                Matcher matcher = pattern.matcher(String.valueOf(source.charAt(i)));
                //jeżeli nie jest zgodne z regexem - nie dodawaj do końcowego stringa
                if(!matcher.matches()){
                    return "";
                }
                //jeżeli spełnione warunki - dodaj znak do wyniku
                sb.append(source.charAt(i));
            }
            //zwróć wynik
            return sb.toString().toUpperCase();
        }
    };

    //Metoda pokazująca ostrzeżenia
    public void showToast(String text){
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    //Zapis instancji stanu - przed obrotem
    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putParcelable("bitmap", drawHandle.getBitmap());
        super.onSaveInstanceState(outState);
    }

    //Przywrócenie instancji stanu - po obrocie
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        Bitmap bitmap = savedInstanceState.getParcelable("bitmap");
        drawHandle.setBitmap(bitmap);
        drawHandle.rd = true;
        super.onRestoreInstanceState(savedInstanceState);
    }

}
