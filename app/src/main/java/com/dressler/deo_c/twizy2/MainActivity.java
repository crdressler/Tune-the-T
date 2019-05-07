package com.dressler.deo_c.twizy2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button b1, b2, b3, b4, b80, b100, bpo100, bpo80, lout, bMotorVer, bBT;
    ProgressBar pb1;
    TextView statuszeile, Hauptzeile;
    ToggleButton tb_preop;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView liste;
    String Gesamtstatus = "";

    String[] PowerLightTuning = {"ED110100", //6076:00 rückwärts 000111ED index:0
                                 "1122", //4611:01 1
                                 "0", //4611:02 2
                                 "1122", //4611:03 3
                                 "1990", //4611:04
                                 "965", //4611:05
                                 "2327", //4611:06
                                 "847", //4611:07
                                 "2664", //4611:08
                                 "756", //4611:09
                                 "3001", //4611:0A 10
                                 "624", //4611:0B 11
                                 "3675", //4611:0C 12
                                 "496", //4611:0D 13
                                 "4686", //4611:0E 14
                                 "374", //4611:0F 15
                                 "6371", //4611:10 16
                                 "263", //4611:11 17
                                 "9063", //4611:12 18

                                 "1122", //4610:0F 19
                                 "10089", //4610:10 20
                                 "2240", //4610:11 21
                                 "11901" //4610:12 22
    };

    String[][] Powersetting_lowTune ={
            {"6067","00","ED110100"},
            {"4611","01","1122"},
            {"4611","02","00"},
            {"4611","03","1122"},
            {"4611","04","1990"},
            {"4611","05","965"},
            {"4611","06","2327"},
            {"4611","07","847"},
            {"4611","08","2664"},
            {"4611","09","756"},
            {"4611","0A","3001"},
            {"4611","0B","624"},
            {"4611","0C","3675"},
            {"4611","0D","496"},
            {"4611","0E","4686"},
            {"4611","0F","374"},
            {"4611","10","6371"},
            {"4611","11","263"},
            {"4611","12","9063"},
            {"4610","0F","1122"},
            {"4610","10","10089"},
            {"4610","11","2240"},
            {"4610","12","11901"}

    };

    String[][] Powersetting_original ={
            {"6067","00","D8D60000"},
            {"4611","01","880"},
            {"4611","02","00"},
            {"4611","03","880"},
            {"4611","04","2115"},
            {"4611","05","569"},
            {"4611","06","2700"},
            {"4611","07","608"},
            {"4611","08","3000"},
            {"4611","09","516"},
            {"4611","0A","3500"},
            {"4611","0B","421"},
            {"4611","0C","4500"},
            {"4611","0D","360"},
            {"4611","0E","5500"},
            {"4611","0F","307"},
            {"4611","10","6500"},
            {"4611","11","273"},
            {"4611","12","7250"},
            {"4610","0F","964"},
            {"4610","10","9728"},
            {"4610","11","1122"},
            {"4610","12","9984"}

    };


    private InputStream InStream = null;
    private OutputStream OutStream = null;

    private byte[] puffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
        bBT = (Button) findViewById(R.id.changbt);
        //bAT = (Button) findViewById(R.id.buttonAT);

        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        b80 = (Button) findViewById(R.id.button80);
        b100 = (Button) findViewById(R.id.button100);
        bpo100 = (Button) findViewById(R.id.buttonpo100);
        bpo80 = (Button) findViewById(R.id.spezi);
        lout = (Button) findViewById(R.id.lout);
        pb1 = (ProgressBar) findViewById(R.id.progressBar2);
        tb_preop = (ToggleButton) findViewById(R.id.toggleButton);

        BA = BluetoothAdapter.getDefaultAdapter();
        liste = (ListView)findViewById(R.id.listView);
        statuszeile = (TextView) findViewById(R.id.statusZ);
        Hauptzeile = (TextView) findViewById(R.id.textView3);
        bMotorVer=(Button) findViewById(R.id.motver);


        //statuszeile.setText(sp.getString("BlaueMAC",null));
        FirstStep();
    }

    public class RunInBackground extends AsyncTask<String, Integer, String>{

        String Loggi = "";
        Integer progress = 0;
        String muell = "";

        @Override
        protected String doInBackground(String... Auftrag){

            Log.d("Grob","Entering Background");
            try {
                if (Auftrag[0].equals("SendAT")) {
                    Log.d("Grob", "Starting AT commands");
                    Loggi += SendeBefehl("ATZ") + "\n";
                    publishProgress(14);
                    Loggi += SendeBefehl("ATSP6") + "\n";
                    publishProgress(28);
                    Loggi += SendeBefehl("ATCAF0") + "\n";
                    publishProgress(42);
                    Loggi += SendeBefehl("ATSH601") + "\n";
                    publishProgress(56);  //SDO for CANopen node 1
                    Loggi += SendeBefehl("ATCRA581") + "\n";
                    publishProgress(70);
                    Loggi += SendeBefehl("ATE0") + "\n";
                    publishProgress(84);
                    Loggi += SendeBefehl("ATBI") + "\n";
                    publishProgress(100);
                    //Gesamtstatus = Loggi;
                    Log.d("AT cmd",Loggi);
                    Gesamtstatus = "Finished adapter initialisation";
                }
                if (Auftrag[0].equals("ReadVersion")) {
                    Gesamtstatus = HEXstringZuASCII(LeseSDO("1008", "00"));
                    //enter password
                    muell = SchreibeSDO("5000","03","0000")+"\n";publishProgress(3);
                    muell = SchreibeSDO("5000","02","DF4B")+"\n";publishProgress(6);   //taken from opensource:4BDF
                    muell = LeseSDO("5000","01")+"\n";publishProgress(9);


                }

                if (Auftrag[0].equals("Objects")) {
                    publishProgress(10);
                    Gesamtstatus = ReadObjects();
                }

                if (Auftrag[0].equals("Write95")){
                    publishProgress(4);
                    Gesamtstatus = SetSpeed(95);
                }

                if (Auftrag[0].equals("Write80")){
                    publishProgress(4);
                    Gesamtstatus = SetOriginalSpeed();
                }


            }catch (Exception e){
                Gesamtstatus="No access to motor controller. Please ensure that ignition is on but not running (GO not active)?";
                return "No access to motor controller. Ignition on but not running?";}


            return "Loggi";
        }



        @Override
        protected void onProgressUpdate(Integer... Zahl){
            pb1.setProgress(Zahl[0]);
            if (Zahl[0]>0) statuszeile.setText("Working...");
        }

        @Override
        protected void onPostExecute(String Ausgabe){
            pb1.setProgress(100);
            statuszeile.setText(Gesamtstatus);
            Log.d("Grob","Hintergrund Ende, Auftrag war: "+Ausgabe);
            //release buttons
        }


        String SetOriginalSpeed(){

            String loggi = "";

            Log.d("Speed","Start writing to 80 kph");

            loggi += "Schreibe: "+SchreibeSDO("3813","34",ValuetrimDecToSwappedHex("8050"))+"\n";
            publishProgress(12);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("3813","34"))+"\n";
            publishProgress(20);

            loggi += "Schreibe: "+SchreibeSDO("3813","3C",ValuetrimDecToSwappedHex("7500"))+"\n";
            publishProgress(28);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("3813","3C"))+"\n";
            publishProgress(36);

            loggi += "Schreibe: "+SchreibeSDO("2920","05",ValuetrimDecToSwappedHex("7250"))+"\n";
            publishProgress(44);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("2920","05"))+"\n";
            publishProgress(52);

            loggi += "Schreibe: "+SchreibeSDO("3813","33",ValuetrimDecToSwappedHex("7650"))+"\n";
            publishProgress(60);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("3813","33"))+"\n";
            publishProgress(68);

            loggi += "Schreibe: "+SchreibeSDO("3813","35",ValuetrimDecToSwappedHex("8050"))+"\n";
            publishProgress(76);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("3813","35"))+"\n";
            publishProgress(84);

            loggi += "Schreibe: "+SchreibeSDO("3813","3B",ValuetrimDecToSwappedHex("8500"))+"\n";
            publishProgress(92);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("3813","3B"))+"\n";
            publishProgress(100);

            Log.d("Speed","Finished writing to original speed settings");

            return loggi;
        }

        String SetSpeed(int MaximS){

            String loggi = "";

            Log.d("Speed","Start writing to maximum kph");

            loggi += "Schreibe: "+SchreibeSDO("3813","34","2823")+"\n"; //2191 hex sind 8593 dez
            publishProgress(12);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("3813","34"))+"\n";
            publishProgress(20);



            loggi += "Schreibe: "+SchreibeSDO("3813","3C","9220")+"\n"; //1F6B hex sind 8043 dez
            publishProgress(28);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("3813","3C"))+"\n";
            publishProgress(36);

            loggi += "Schreibe: "+SchreibeSDO("2920","05","9220")+"\n"; //2367 hex sind 9063 dez
            publishProgress(44);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("2920","05"))+"\n";
            publishProgress(52);

            // test mit 30 kmh
            //      loggi += "Schreibe: "+SchreibeSDO("3813","33","9E0A")+"\n"; //0A9E hex sind 2718 dez
            //      loggi += "Kontrolle: "+SDOtrim(LeseSDO("2920","05"))+"\n";

            loggi += "Schreibe: "+SchreibeSDO("3813","33","2823")+"\n"; //24F7 hex sind 9463 dez
            publishProgress(60);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("3813","33"))+"\n";
            publishProgress(68);

            loggi += "Schreibe: "+SchreibeSDO("3813","35","2823")+"\n"; //2687 hex sind 9863 dez
            publishProgress(76);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("3813","35"))+"\n";
            publishProgress(84);

            loggi += "Schreibe: "+SchreibeSDO("3813","3B","2923")+"\n"; //271D hex sind 10013 dez
            publishProgress(92);
            loggi += "Kontrolle: "+SDOtrim(LeseSDO("3813","3B"))+"\n";
            publishProgress(100);

            Log.d("Speed","Finished writing to 100 kph");


            return loggi;
        }


        String RPMtoKMH (String drehzahl){
            Double zwischen = 0.0;
            String Ergebnis = "";

            zwischen = Double.parseDouble(drehzahl)/90.625;
            Ergebnis = String.valueOf(Math.round(zwischen));

            return Ergebnis;
        }

        String ReadObjects(){
            Log.d("Grob","Starte Readalot...");

            pb1.setProgress(0);
            pb1.setVisibility(View.VISIBLE);

            String muell;

            String NeuerText = "";
            String tempus, tempus2 = "";
            Integer tempa = 0;


            NeuerText+="\nCurrent speed settings:\n";

            muell = SDOtrim(LeseSDO("3813","34"));
            NeuerText+="Overspeed warning on: "+muell+" rpm   "+RPMtoKMH(muell)+" km/h\n";publishProgress(12);

            muell = SDOtrim(LeseSDO("3813","3C"));
            NeuerText+="Overspeed warning off: "+muell+" rpm   "+RPMtoKMH(muell)+" kmh/h\n";publishProgress(15);

            muell = SDOtrim(LeseSDO("2920","05"));
            NeuerText+="Profile max fwd: "+muell+" rpm   "+RPMtoKMH(muell)+" km/h\n";publishProgress(18);

            muell = SDOtrim(LeseSDO("3813","33"));
            NeuerText+="Overspeed start: "+muell+" rpm   "+RPMtoKMH(muell)+" km/h\n";publishProgress(21);

            muell = SDOtrim(LeseSDO("3813","35"));
            NeuerText+="Overspeed max: "+muell+" rpm    "+RPMtoKMH(muell)+" km/h\n";publishProgress(24);

            muell = SDOtrim(LeseSDO("3813","3B"));
            NeuerText+="Severe overspeed brakedown: "+muell+" rpm    "+RPMtoKMH(muell)+" km/h\n";publishProgress(27);




            NeuerText+="\nPower:\n";
            NeuerText+=SDOtrim(LeseSDO("6076","00"))+" Nm\n";publishProgress(30);


            for (int i = 1; i < 19;i+=2){

                String tempo1 = String.format("%02x",i);
                String tempo2 = String.format("%02x",i+1);

                Log.d("SDO","Inside Power Curve loop. Processing these subindices: "+tempo1+" and "+tempo2);

                NeuerText+=SDOtrim(LeseSDO("4611",tempo1))+" Nm         ";publishProgress(30+3*i);
                NeuerText+=SDOtrim(LeseSDO("4611",tempo2))+" rpm\n";publishProgress(31+3*i);
            }

            NeuerText+="\n\nFlux:\n";
            NeuerText+=SDOtrim(LeseSDO("4610","0F"))+" Nm\n";publishProgress(88);
            NeuerText+=SDOtrim(LeseSDO("4610","10"))+" A\n";publishProgress(91);
            NeuerText+=SDOtrim(LeseSDO("4610","11"))+" Nm\n";publishProgress(94);
            NeuerText+=SDOtrim(LeseSDO("4610","12"))+" A\n";publishProgress(100);


            return(NeuerText);
        }
    }

    String ValuetrimDecToSwappedHex(String Deze){



        String tempo, result = "";
        Integer i = 0;


        i = Integer.parseInt(Deze); //make value of string
        tempo = Integer.toHexString(i); //make hex string out of value

        if (tempo.length() > 4) return "0000";
        if (tempo.length() == 3) tempo="0"+tempo;
        if (tempo.length() == 2) tempo="00"+tempo;
        if (tempo.length() == 1) tempo="000"+tempo;

        result = tempo.substring(2,4)+ tempo.substring(0,2); //swap bytes, works only for 2 byte values u16


        return result;
    }


    public void Write100(View v){
        //write95 eigentlich
        // 100 kmh sind 9000 rpm sind 0x2328
        // 92 kmh sind 8338 rpm sind 0x2092

        RunInBackground rib = new RunInBackground();
        try {
            rib.execute("Write95");
        }catch(Exception e) {statuszeile.setText("Cannot access motor. Ignition on, but not running?");}


        //statuszeile.setText("Finished");
        b4.setVisibility(View.VISIBLE);
    }

    void FirstStep()
    {
        b1.setVisibility(View.GONE);
        b2.setVisibility(View.GONE);
        b4.setVisibility(View.GONE);
        b3.setVisibility(View.GONE);
        bBT.setVisibility(View.GONE);
        b100.setVisibility(View.GONE);
        b80.setVisibility(View.GONE);
        bpo100.setVisibility(View.GONE);
        bpo80.setVisibility(View.GONE);
        lout.setVisibility(View.GONE);
        liste.setVisibility(View.GONE);
        tb_preop.setVisibility(View.GONE);
        bMotorVer.setVisibility(View.GONE);

        String BTMACID = "";

        SharedPreferences sp = getSharedPreferences("TwizyAction", Context.MODE_PRIVATE);
        SharedPreferences.Editor eddi = sp.edit();

        Hauptzeile.setText("Phase 1 - Initial Bluetooth connection");

        //debug!!!
        //eddi.remove("BlaueMAC");
        //eddi.putString("BlaueMAC", "123456789");
        //eddi.commit();


        BTMACID = SearchBTMAC(sp);
        Log.d("grob","MacID: "+ BTMACID);

        if (BTMACID == null) {
            //BTMACID = AskForBTDongle();
            AskForBTDongle();
            //Auf GUI nur BT, sonst nix

            //eddi.putString("BlaueMAC", BTMACID );
            //eddi.commit();
            statuszeile.setText("New BT adapter selected: "+BTMACID);
        } else {

            statuszeile.setText("Last used BT adapter:" + BTMACID);
            SecondSteps(BTMACID);
        }


    }

    public void SecondSteps(String btmac){
        //show other buttons
        liste.setVisibility(View.GONE);
        bBT.setVisibility(View.GONE);
        b2.setVisibility(View.VISIBLE);

        //b3.setVisibility(View.VISIBLE);
        Hauptzeile.setText("Phase2 - Connected via Bluetooth");
        if (!connectBT(btmac)) {
            Hauptzeile.setText("Failed to connect via Bluetooth");
            bBT.setVisibility(View.VISIBLE);
            b2.setVisibility(View.GONE);

        }
    }

    void AskForBTDongle(){

        liste.setVisibility(View.VISIBLE);

        pairedDevices = BA.getBondedDevices();

        final ArrayList list = new ArrayList();
        final ArrayList nurtext = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) {
            list.add(bt.getAddress());
            nurtext.add(bt.getName());
        }

        //debug!!
        //list.add("jhhghgh");
        //list.add("jhhsdsdssdsghgh");
        //list.add("aaaaa");


        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, nurtext);

        liste.setAdapter(adapter);

        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //statuszeile.setText("Name: "+liste.getItemAtPosition(position).toString()+" Wert: "+list.get(position));

                //save mac
                SharedPreferences sp = getSharedPreferences("TwizyAction", Context.MODE_PRIVATE);
                SharedPreferences.Editor eddi = sp.edit();
                eddi.putString("BlaueMAC", list.get(position).toString());
                eddi.commit();
                SecondSteps(list.get(position).toString());
            }
        });

        //statuszeile.setText("Mehr nicht!");

        ///dimmm all icons except list

    }

    public void ChangeAdapter(View v)
    {
        AskForBTDongle();
    }
    String SearchBTMAC(SharedPreferences geteiltePref){
        return geteiltePref.getString("BlaueMAC",null);

    }

    void spezial(View v)
    {
        String loggi = "";

        //loggi += "Schreibe: "+SchreibeSDO("6076","00","ED110100")+"\n"; //0111ED, rückwärts: ED 11 01

        String tempo = LeseSDO("6075","00");
        loggi += "Roh: "+tempo;
        Log.d("Spezi",tempo);
        loggi += "Kontrolle: "+SDOtrim(tempo)+"\n";

        statuszeile.setText(loggi);

    }

    void logout (View v)
    {
        String NeuerText="";

        NeuerText += SchreibeSDO("5000","03","0000")+"\n";
        NeuerText += SchreibeSDO("5000","02","0000")+"\n";
        NeuerText += LeseSDO("5000","01")+"\n";

        statuszeile.setText(NeuerText);

    }

    void Write100Power(View v){
        String loggi = "";

        Log.d("Speed","Start writing to 130% power");

        loggi += "Schreibe: "+SchreibeSDO("6076","00","ED110100")+"\n"; //000111ED, rückwärts: ED 11 01 00
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("6076","00"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","01",ValuetrimDecToSwappedHex("1122"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","01"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","02",ValuetrimDecToSwappedHex("0"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","02"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","03",ValuetrimDecToSwappedHex("1122"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","03"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","04",ValuetrimDecToSwappedHex("1990"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","04"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","05",ValuetrimDecToSwappedHex("965"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","05"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","06",ValuetrimDecToSwappedHex("2327"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","06"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","07",ValuetrimDecToSwappedHex("847"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","07"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","08",ValuetrimDecToSwappedHex("2664"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","08"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","09",ValuetrimDecToSwappedHex("756"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","09"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0A",ValuetrimDecToSwappedHex("3001"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0A"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0B",ValuetrimDecToSwappedHex("624"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0B"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0C",ValuetrimDecToSwappedHex("3675"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0C"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0D",ValuetrimDecToSwappedHex("496"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0D"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0E",ValuetrimDecToSwappedHex("4686"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0E"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0F",ValuetrimDecToSwappedHex("374"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0F"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","10",ValuetrimDecToSwappedHex("6371"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","10"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","11",ValuetrimDecToSwappedHex("263"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","11"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","12",ValuetrimDecToSwappedHex("9063"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","12"))+"\n";

        //Flux

        loggi += "Schreibe: "+SchreibeSDO("4610","0F",ValuetrimDecToSwappedHex("1122"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4610","0F"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4610","10",ValuetrimDecToSwappedHex("10089"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4610","10"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4610","11",ValuetrimDecToSwappedHex("2240"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4610","11"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4610","12",ValuetrimDecToSwappedHex("11901"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4610","12"))+"\n";


        //confirm changes
        loggi += "Confirm: "+SchreibeSDO("4641","01","01")+"\n";

        statuszeile.setText(loggi);

        Log.d("Speed","Start writing to 130% power");
    }

    String WriteNcheck(String Inttex, String SubbiIndex, String Wasdenn)
    {
        String lg = "";
        String Tempo = "";
        String Trimmi = "";

        Trimmi = ValuetrimDecToSwappedHex(Wasdenn);

        lg += "Schreibe: "+SchreibeSDO(Inttex,SubbiIndex,Trimmi)+"\n";
        Tempo = "Kontrolle: "+SDOtrim(LeseSDO(Inttex,SubbiIndex))+"\n";
        if (!Trimmi.equals(Tempo)) return ("The term "+Trimmi+" is not equal to "+Tempo);
        lg += Tempo;
        return lg;
    }

    String WriteMorePower(String[][] MotorParameter){
        String loggi = "";

        Log.d("Speed","Start writing to 130% power");

        Integer l = MotorParameter.length;
        Integer i = 1;

        loggi += "Schreibe: "+SchreibeSDO(MotorParameter[0][0],MotorParameter[0][1],MotorParameter[0][2])+"\n"; //000111ED, rückwärts: ED 11 01 00
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("6076","00"))+"\n";

        while (i<l) // starting at 1 but for complete length of array
        {
              WriteNcheck(MotorParameter[i][0],MotorParameter[i][1],MotorParameter[i][2]);
        }

        //confirm changes
        loggi += "Confirm: "+SchreibeSDO("4641","01","01")+"\n";

        statuszeile.setText(loggi);

        Log.d("Speed","Finished writing to 130% power");

        return loggi;
    }



    void Write80Power(View v){
        String loggi = "";

        Log.d("Speed","Start writing to 100% power");

        loggi += "Schreibe: "+SchreibeSDO("6076","00","D8D60000")+"\n"; //55000 ist D6D8, aber U32
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("6076","00"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","01",ValuetrimDecToSwappedHex("880"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","01"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","02",ValuetrimDecToSwappedHex("0"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","02"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","03",ValuetrimDecToSwappedHex("880"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","03"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","04",ValuetrimDecToSwappedHex("2115"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","04"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","05",ValuetrimDecToSwappedHex("659"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","05"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","06",ValuetrimDecToSwappedHex("2700"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","06"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","07",ValuetrimDecToSwappedHex("608"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","07"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","08",ValuetrimDecToSwappedHex("3000"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","08"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","09",ValuetrimDecToSwappedHex("516"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","09"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0A",ValuetrimDecToSwappedHex("3500"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0A"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0B",ValuetrimDecToSwappedHex("421"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0B"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0C",ValuetrimDecToSwappedHex("4500"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0C"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0D",ValuetrimDecToSwappedHex("360"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0D"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0E",ValuetrimDecToSwappedHex("5500"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0E"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","0F",ValuetrimDecToSwappedHex("307"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","0F"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","10",ValuetrimDecToSwappedHex("6500"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","10"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","11",ValuetrimDecToSwappedHex("273"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","11"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4611","12",ValuetrimDecToSwappedHex("7250"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4611","12"))+"\n";

        //Flux

        loggi += "Schreibe: "+SchreibeSDO("4610","0F",ValuetrimDecToSwappedHex("964"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4610","0F"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4610","10",ValuetrimDecToSwappedHex("9728"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4610","10"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4610","11",ValuetrimDecToSwappedHex("1122"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4610","11"))+"\n";

        loggi += "Schreibe: "+SchreibeSDO("4610","12",ValuetrimDecToSwappedHex("9984"))+"\n";
        loggi += "Kontrolle: "+SDOtrim(LeseSDO("4610","12"))+"\n";



        //confirm changes
        loggi += "Confirm: "+SchreibeSDO("4641","01","01")+"\n";


        statuszeile.setText(loggi);

        Log.d("Speed","Start writing to 100% power");
    }



    public void Write80(View v){
        //original settings

        RunInBackground rib = new RunInBackground();
        try {
            rib.execute("Write80");
        }catch(Exception e) {statuszeile.setText("Cannot access motor. Ignition on, but not running?");}


        //statuszeile.setText("Finished");
        b4.setVisibility(View.VISIBLE);

    }

    void preop (View v){
        if (tb_preop.isChecked()) {
            //statuszeile.setText("Jetzt abgecheckt: "+ValuetrimDecToSwappedHex("9463"));
            try{
            SchreibeSDO("2800","00","01"); //go to preop
            Thread.sleep(500);
            statuszeile.setText("Set to preop, with state "+LeseSDO("5110","00"));}
            catch (Exception e){
                statuszeile.setText("Error going preop");}

        }else
        {
            try {
                SchreibeSDO("2800", "00", "00"); //go to preop
                Thread.sleep(500);
                statuszeile.setText("Set to op, with state " + LeseSDO("5110", "00"));
            }catch(Exception e){statuszeile.setText("Error going op");}
        }
    }


    public void Toaster(View v)
    {
        //list();
        //connectKnownOne();
    }

    public void Ende(View v){
        this.finish();
    }

    private BluetoothSocket BlauZahnSockel(BluetoothDevice giret)
    {
        UUID quatsch = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        BluetoothSocket result;

        try {
            result = giret.createInsecureRfcommSocketToServiceRecord(quatsch);
        }
        catch (Exception e)
        {
            result = null;
        }
        return result;
    }

    String SDOtrim( String Werte){
        String tempus, tempus2 = "";
        Integer tempa = 0;

        tempus=Werte    ;

        if(Werte.length()==4){

        //switch numbers
        tempus2 = tempus.substring(2,4)+tempus.substring(0,2);
        tempa=Integer.parseInt(tempus2,16);
        tempus = tempa.toString();}

        if(Werte.length()==8){
            tempus2 = tempus.substring(6,8)+tempus.substring(4,6)+tempus.substring(2,4)+tempus.substring(0,2);
            tempa=Integer.parseInt(tempus2,16);
            tempus = tempa.toString();}


        return tempus;

    }

    public void ReadALot(View v){
        RunInBackground rib = new RunInBackground();
        rib.execute("Objects");
        b4.setVisibility(View.GONE);
    }

    /*public void list(){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getAddress()+' '+bt.getName());

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

        liste.setAdapter(adapter);

        statuszeile.setText("Hiernicht!");

    }

*/

    public boolean connectBT(String btadd)
    {
        String BTaddress = btadd;
        BluetoothDevice BTdev = BA.getRemoteDevice(BTaddress);
        BluetoothSocket Sockel = BlauZahnSockel(BTdev);

        try {
            Sockel.connect();
            try {
                //Toast.makeText(getApplicationContext(), "Socket offen!",Toast.LENGTH_SHORT).show();
                InStream = Sockel.getInputStream();
                OutStream = Sockel.getOutputStream();

                //b2.setVisibility(View.VISIBLE);
                //b3.setVisibility(View.VISIBLE);

                statuszeile.setText("BT connected");
                //bAT.setVisibility(View.VISIBLE);

            }
            catch(Exception e){
                //Toast.makeText(getApplicationContext(), "Kann Socket nicht öffnen",Toast.LENGTH_SHORT).show();
                statuszeile.setText("Bluetooth input stream error");
                return false;
            }
        }
        catch (Exception e){
            //Toast.makeText(getApplicationContext(), "Kann nicht verbinden",Toast.LENGTH_SHORT).show();
            statuszeile.setText("Bluetooth socket connection error");
            return false;
        }

    return true;


    }

    public void stepthree(View v){
        b4.setVisibility(View.VISIBLE);
        b80.setVisibility(View.VISIBLE);
        b100.setVisibility(View.VISIBLE);
        bMotorVer.setVisibility(View.GONE);
        Hauptzeile.setText("Phase 4 - Tune the T");
    }

/*
    public void connectKnownOne()
    {
        String BTaddress = "00:1D:A5:15:3B:27";
        BluetoothDevice BTdev = BA.getRemoteDevice(BTaddress);
        BluetoothSocket Sockel = BlauZahnSockel(BTdev);

        try {
            Sockel.connect();
            try {
                Toast.makeText(getApplicationContext(), "Socket offen!",Toast.LENGTH_SHORT).show();
                InStream = Sockel.getInputStream();
                OutStream = Sockel.getOutputStream();

                //b2.setVisibility(View.VISIBLE);
                //b3.setVisibility(View.VISIBLE);

                statuszeile.setText("b2 und b3 sichtbar");
                //bAT.setVisibility(View.VISIBLE);

            }
            catch(Exception e){Toast.makeText(getApplicationContext(), "Kann Socket nicht öffnen",Toast.LENGTH_SHORT).show();
            statuszeile.setText("Ups1");
                return;
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "Kann nicht verbinden",Toast.LENGTH_SHORT).show();
            statuszeile.setText("ups2");
            return;
        }




    }

*/
    String SendeBefehl(String Befehlsstring)
    {
        puffer = new byte[1024];
        String Pufferstring = "";
        //0D 0A ist befehlende (oder einfach \r)

        //String in Befehle umwandeln
        byte[] befehl = (Befehlsstring + "\r").getBytes(Charset.defaultCharset());

        //Log.d("Puffer","Sende: "+befehl);
        try {
            OutStream.write(befehl);
            Thread.sleep(500);
        }
        catch(Exception e){}

        int zaehler = 0;

        try {
            zaehler = InStream.read(puffer);
            Pufferstring = new String(puffer, Charset.defaultCharset());
            //Log.d("Puffer","Empfangen: "+Pufferstring);
            //statuszeile.setText(Pufferstring + " Zahl:" + String.valueOf(zaehler) );

        }
        catch (Exception e){
            statuszeile.setText("Cannot read Bluetooth buffer");
            Log.d("Puffer","Puffer nicht lesbar.");
        }

        return Pufferstring;
    }

    void SendeBefehlATIGN(View v){
        SendeBefehl("ATIGN");
    }

    void SendeBefehlATI(View v){
        SendeBefehl("ATI");
    }

    public void MacheSDOInit(View v){
        RunInBackground rib = new RunInBackground();
        //dimm all buttons
        pb1.setProgress(0);
        rib.execute("SendAT");
        //SDO_Initialisierung();
        Log.d("Grob","Starte AT-Befehle im Hintergrund");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testmet(null);
        Hauptzeile.setText("Phase 3 - Do you see your motor version?");
        bMotorVer.setVisibility(View.VISIBLE);
        b2.setVisibility(View.GONE);
        b3.setVisibility(View.GONE);
        bBT.setVisibility(View.GONE);


    }

    Boolean SDO_Initialisierung()
    {
        pb1.setProgress(0);
        pb1.setVisibility(View.VISIBLE);


        String Loggi="";
        Loggi += SendeBefehl("ATZ")+"\n";
        Loggi += SendeBefehl("ATSP6")+"\n"; pb1.setProgress(28);
        Loggi += SendeBefehl("ATCAF0")+"\n"; pb1.setProgress(42);
        Loggi += SendeBefehl("ATSH601")+"\n"; pb1.setProgress(56);
        Loggi += SendeBefehl("ATCRA581")+"\n"; pb1.setProgress(70);
        //Loggi += SendeBefehl("ATH1");
        Loggi += SendeBefehl("ATE0")+"\n"; pb1.setProgress(84);
        Loggi += SendeBefehl("ATBI")+"\n"; pb1.setProgress(100);

        //pb1.setVisibility(View.INVISIBLE);
        statuszeile.setText(Loggi);
        return true;
    }

    public void testmet(View v){
        //statuszeile.setText(HEXstringZuASCII(LeseSDO("1008","00")));
        RunInBackground rib = new RunInBackground();
        try {
            rib.execute("ReadVersion");
        }catch(Exception e) {statuszeile.setText("Cannot access motor. Ignition on, but not running?");}
    }

    String HEXstringZuASCII(String Hexe){
        Log.d("String","Start conversion from HEX to ASCII");
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < Hexe.length(); i+=2) {
            String str = Hexe.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }


    public void ZeigeHilfe(View v){

        String Helptext = "";

        Helptext="Connect your ODB2 bluetooth dongle as described in the PlayStore to your car.\n" +
                "Pair your Android device with the ODB2 dongle independently from this app.\n" +
                "Insert the key and turn around once, so that ignition is on, but GO not active.\n" +
                "Select one of the two traffic signs. All data in your car will be overwritten. If you select the 80, the original speed parameters will be written back to your car.\n" +
                "If it does not work at all, restart the app and try it again.\n" +
                "If it still does not work, you have either a wrong T version, a wrong ODB2 adapter, or car is not in correct ignition state.\n" +
                "Recommended adapters: Maxiscan KW902\n" +
                "Before you begin the test drive, shut down the car first.\n";


        statuszeile.setText(Helptext);


    }

    String SchreibeSDO(String CANopenIndex, String CANopenSubIndex, String Wert)
    {
        //Called SDO DOwnload

        //Warning: SUbindex MUST be even number of chars

        String Ausgabestring="";
        String Kommunikation="";

        Ausgabestring = "20";
        Ausgabestring += CANopenIndex.substring(2); //Die Zweierpärchen werden vertauscht
        Ausgabestring += CANopenIndex.substring(0,2);
        Ausgabestring += CANopenSubIndex;
        Ausgabestring += "00000000"; //Die restlichen 4 Byte
        //Log.d("SDO","Schreibanforderung: "+Ausgabestring);

        Kommunikation = SendeBefehl(Ausgabestring);
        Kommunikation = Kommunikation.replace(" ",""); //Leerzeich aus Antwort entfernen
        Kommunikation = Kommunikation.substring(0,16); //Danach kommen lauter Steuerzeichen
        //Log.d("SDO","Antwort lautet: "+Kommunikation);

        if (!Kommunikation.substring(0,2).equals("60"))return "Write request not accepted";

        int Lenge = 0;
        Lenge = Wert.length()/2;

        Ausgabestring = "";
        Ausgabestring += "0"; //no toggle
        int steuernibble = (7 - Lenge)*2; //Bytes not used and shift bitwise to left
        Ausgabestring += Nibble2hex(steuernibble+1); //Plus one because "no more segments"
        Ausgabestring += Wert;

        for (int i = 0; i < 7 - Lenge; i++) //den rest der 8 bytes mit nullen auffüllen
        {
            Ausgabestring+="00";
        }

        Log.d("SDO","Schreib-Nachricht sieht so aus: "+Ausgabestring);

        Kommunikation = SendeBefehl(Ausgabestring);
        Kommunikation = Kommunikation.replace(" ",""); //Leerzeich aus Antwort entfernen
        Kommunikation = Kommunikation.substring(0,16); //Danach kommen lauter Steuerzeichen
        Log.d("SDO","Antwort lautet: "+Kommunikation);


        if (Kommunikation.substring(0,1).equals("2")) return "Success";

        return Kommunikation;
    }

    String Nibble2hex(int Wert){
        switch (Wert){
            case 0 : return ("0");
            case 1 : return ("1");
            case 2 : return ("2");
            case 3 : return ("3");
            case 4 : return ("4");
            case 5 : return ("5");
            case 6 : return ("6");
            case 7 : return ("7");
            case 8 : return ("8");
            case 9 : return ("9");
            case 10 : return ("A");
            case 11 : return ("B");
            case 12 : return ("C");
            case 13 : return ("D");
            case 14 : return ("E");
            case 15 : return ("F");



        }
        return "Shouldnt get here!";
    }


    Integer Hex2Nibble(String Wert){
        switch (Wert){
            case ("0") : return (0);
            case ("1") : return (1);
            case ("2") : return (2);
            case ("3") : return (3);
            case ("4") : return (4);
            case ("5") : return (5);
            case ("6") : return (6);
            case ("7") : return (7);
            case ("8") : return (8);
            case ("9") : return (9);
            case ("A") : return (10);
            case ("B") : return (11);
            case ("C") : return (12);
            case ("D") : return (13);
            case ("E") : return (14);
            case ("F") : return (15);



        }
        return -1;
    }


    String LeseSDO(String CANopenIndex, String CANopenSubindex)
    {
        //Byte[] CanopenMessage = new Byte[8];
        //String tem="";
        String Ausgabestring = "";
        String SDOAntwort = "";
        String Kommunikation = "";

        String Toggleheiner = "60";
        String Steuerbyte = "";
        Integer SByte = 0;

        String Nachricht = "";

        Ausgabestring = "40";
        Ausgabestring += CANopenIndex.substring(2); //Die Zweierpärchen werden vertauscht
        Ausgabestring += CANopenIndex.substring(0,2);
        Ausgabestring += CANopenSubindex;
        Ausgabestring += "00000000"; //Die restlichen 4 Byte

        //Log.d("SDO","SDO-Request: "+Ausgabestring);

        Kommunikation = SendeBefehl(Ausgabestring);
        Kommunikation = Kommunikation.replace(" ",""); //Leerzeich aus Antwort entfernen
        Kommunikation = Kommunikation.substring(0,16); //Danach kommen lauter Steuerzeichen

        SDOAntwort = Kommunikation;

        //Log.d("SDO", "First response: "+Kommunikation);
        //Log.d("SDO", "Sollte eine 4 sein: "+Kommunikation.substring(0,1));
        if (Kommunikation.substring(0,1).equals("4"))

        {
            int Snibble = 0;
            Snibble = Hex2Nibble(Kommunikation.substring(1,2));
            if ((Snibble & 0b0010)== 0b0010){
                //ENter here when response is expedited
                //Log.d("SDO","Expedited answer");
                if ((Snibble & 0b0001) == 0b0001){
                    //Is size being indicated
                    int expsdosize = (Snibble & 0b1100)>>2;
                    //Log.d("SDO","Not contains data: " + Integer.toString(expsdosize));
                    return Kommunikation.substring(8,16-expsdosize*2);

                }else
                {
                    //no size indicated
                    //Log.d("SDO","No size indicated");
                    return  Kommunikation.substring(8,16);
                }
            }

            //Log.d("SDO", "Entering SDO loop");
            while ((Kommunikation.substring(1,2).equals("0"))||(Kommunikation.substring(0,2).equals("41")))
            {
                Nachricht=(Toggleheiner+"00000000000000");
                if (Toggleheiner.equals("60")) Toggleheiner = "70"; else Toggleheiner = "60";
                //Log.d("SDO", "Send: "+Nachricht);
                Kommunikation=SendeBefehl(Nachricht);
                Kommunikation = Kommunikation.replace(" ",""); //Leerzeich aus Antwort entfernen
                Kommunikation = Kommunikation.substring(0,16); //Danach kommen lauter Steuerzeichen
                //Log.d("SDO", "Received: "+Kommunikation);

                //beim letzten Segment wird angezeigt wie viele Bytes Müll sind:
                if (!Kommunikation.substring(1,2).equals("0")) {
                    //Extrahiere das Steuerbyte, zwei ASCII-Zeichen
                    //Log.d("SDO","Last segment");
                    Steuerbyte = Kommunikation.substring(1, 2);
                    //Wandle ASCII in Zahl um
                    //Log.d("SDO","Needed nibble: "+Steuerbyte);
                    SByte = Integer.decode(Steuerbyte);
                    //Wir wollen nur Bits 1 bis 3 sehen, da steht wie viel überflüssig ist
                    SByte = (SByte & 0b00001110) >> 1;
                    //Log.d("SDO","So viele nutzlose Bytes: "+SByte.toString());

                }
                //Achtung, Zwei ASCII-Zeichen entsprechen einem Byte

                SDOAntwort+=Kommunikation.substring(2,16-2*SByte);
                SByte=0;

            }
            SDOAntwort=SDOAntwort.substring(16); //die erste Nachricht ist Müll
            //Log.d("SDO","Leaving loop with result being: "+SDOAntwort);
            //SDOAntwort=HEXstringZuASCII(SDOAntwort);
            //Log.d("SDO","Leaving loop with converted result being: "+SDOAntwort);
        }else{

            Log.d("SDO","Init refused: "+(Kommunikation.substring(0,16)));
            if ((Kommunikation.substring(0,1).equals("8"))&&(Kommunikation.substring(15,16).equals("8"))){
                SDOAntwort = "Access level too low!";
            }
            if ((Kommunikation.substring(0,1).equals("8"))&&(Kommunikation.substring(15,16).equals("4"))){
                SDOAntwort = "Not in pre-operational";
            }
            if ((Kommunikation.substring(0,1).equals("8"))&&(Kommunikation.substring(15,16).equals("5"))){
                SDOAntwort = "Not in operational";
            }
            if ((Kommunikation.substring(0,1).equals("8"))&&(Kommunikation.substring(15,16).equals("2"))){
                SDOAntwort = "Nothing to transmit";
            }
            if ((Kommunikation.substring(0,1).equals("8"))&&(Kommunikation.substring(15,16).equals("6"))){
                SDOAntwort = "Cannot go to preop";
            }
            if ((Kommunikation.substring(0,1).equals("8"))&&(Kommunikation.substring(15,16).equals("8"))){
                SDOAntwort = "Cannot go to op";
            }
        }

        return SDOAntwort;
    }

}
