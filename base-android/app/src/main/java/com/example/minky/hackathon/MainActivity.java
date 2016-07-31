package com.example.minky.hackathon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "bluetooth2";
    public static final int ADD_SYS = 100;
    Button btnOn, btnOff;
    TextView txtArduino;
    TextView macAddress;
    TextView textAr;
    Handler h;
    ImageButton addSystem;
    ImageView logo;
    final int RECIEVE_MESSAGE = 1;		// Status  for Handler
    final int CONNECT_DEVICE = 2;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket1 = null;
    private BluetoothSocket btSocket2 = null;
    private BluetoothSocket btSocket3 = null;
    private BluetoothSocket btSocket4 = null;
    private BluetoothSocket btSocket5 = null;
    private BluetoothSocket btSocket6 = null;
    private BluetoothSocket btSocket7 = null;

    private static final String doorAlert = "Door Alert!";
    private static final String pillAlert = "Pill Alert!";
    private static final String tempAlert = "Temperature Alert!";
    private static final String fallAlert = "Emergency Alert!";

    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("fb36491d-7c21-40ef-9f67-a63237b5bbea");
    // MAC-address of Bluetooth module (you must edit this line)
    private String address = "";

    List<BluetoothSocket> bluetoothSocketList;
    List<String> uuidList;
    int uuidCount=0;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    List<BluetoothDevice> connectedDevices;
    FirebaseDatabase database;
    DatabaseReference myRef;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();
        listeners();
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:													// if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);					// create string from bytes array
                        sb.append(strIncom);												// append string
                        int endOfLineIndex = sb.indexOf("\r\n");							// determine the end-of-line
                        if (endOfLineIndex > 0) { 											// if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);				// extract string
                            sb.delete(0, sb.length());// and clear
                            Log.e("DIST",sbprint);
                            textAr.setText("Data received: "+sbprint);
                            decipherData(sbprint);
/*                            if(sbprint.equals("True")) {
                                txtArduino.setText("Triggered. Something within 10cm");
                                textAr.setText("Data from Arduino: " + sbprint);
                                //DatabaseReference myRef = database.getReference("message");
                                //myRef.setValue("Hello, World!");
                            }else{
                                txtArduino.setText("False");
                                textAr.setText("False");
                            }*/
                        }
                        break;
                    case CONNECT_DEVICE:
                        BluetoothDevice device = (BluetoothDevice)msg.obj;
                        connectedDevices.add(device);
                        bluetoothSocketList.add(bluetoothSocketList.get(uuidCount));
                        uuidCount = uuidCount +1;
                        arrayAdapter.add(device.getName());
                        Log.d("FIREBASE","Reference obtained");
                        myRef = database.getReference("devices");
                        myRef.setValue(device.getName());
                        myRef = database.getReference("message");
                }
            };
        };
    }

    public void initialise(){
        uuidList = new ArrayList<>();
        uuidList.add("fb36491d-7c21-40ef-9f67-a63237b5bbea");
        uuidList.add("00001101-0000-1000-8000-00805F9B34FB");
        uuidList.add("e442e09a-51f3-4a7b-91cb-f638491d1412");
        uuidList.add("a81d6504-4536-49ee-a475-7d96d09439e4");
        uuidList.add("aa91eab1-d8ad-448e-abdb-95ebba4a9b55");
        uuidList.add("4d34da73-d0a4-4f40-ac38-917e0a9dee97");
        uuidList.add("5e14d4df-9c8a-4db7-81e4-c937564c86e0");

        Log.d("UUID",uuidList.get(0));
        Log.d("UUID",uuidList.get(1));
        logo = (ImageView)findViewById(R.id.imageView);
        logo.requestLayout();
        logo.getLayoutParams().height = logo.getLayoutParams().width;
        txtArduino = (TextView) findViewById(R.id.txtArduino);		// for display the received data from the Arduino
        textAr = (TextView)findViewById(R.id.textView4);
        macAddress = (TextView)findViewById(R.id.textView3);
        macAddress.setText(address);
        addSystem = (ImageButton)findViewById(R.id.button4);
        listView = (ListView)findViewById(R.id.listView3);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,retrieveSystem());
        listView.setAdapter(arrayAdapter);
        bluetoothSocketList = new ArrayList<>();
        bluetoothSocketList.add(btSocket1);
        bluetoothSocketList.add(btSocket2);
        bluetoothSocketList.add(btSocket2);
        bluetoothSocketList.add(btSocket3);
        bluetoothSocketList.add(btSocket4);
        bluetoothSocketList.add(btSocket5);
        bluetoothSocketList.add(btSocket6);
        bluetoothSocketList.add(btSocket7);

        connectedDevices = new ArrayList<>();
        btAdapter = BluetoothAdapter.getDefaultAdapter();		// get Bluetooth adapter
        checkBTState();
        database = FirebaseDatabase.getInstance();
        //Log.d("FIREBASE","Reference obtained");
        myRef = database.getReference("message");
        //notifyPopUp("My notification","Hello World!");
        //notifyPopUp("NodeCare","Grandma has fallen!");
    }

    public void notifyPopUp(String title, String messageBody){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.bluetooth_logo)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_ALL);

// Creates an explicit intent for an Activity in your app
        //Intent resultIntent = new Intent(this, ResultActivity.class);

        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        //stackBuilder.addParentStack(ResultActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        //stackBuilder.addNextIntent(resultIntent);
        //PendingIntent resultPendingIntent =
        //       stackBuilder.getPendingIntent(
        //             0,
        //           PendingIntent.FLAG_UPDATE_CURRENT
        //   );
        //mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void listeners(){
        addSystem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
                startActivityForResult(intent,ADD_SYS);
            }
        });
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("CHILDEVENTLISTENER",dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Log.d("CHILDEVENTLISTENER", dataSnapshot.getKey());
                if (dataSnapshot.getValue().equals("true")) {
                    if (dataSnapshot.getKey().equals("doora")) {
                        notifyPopUp(doorAlert, "Grandma has left the door open!");
                        myRef.child("doora").setValue("false");
                    } else if (dataSnapshot.getKey().equals("pill")) {
                        notifyPopUp(pillAlert, "Grandma has taken her pills!");
                        myRef.child("pill").setValue("false");
                    } else if (dataSnapshot.getKey().equals("temp")) {
                        notifyPopUp(tempAlert, "Grandma is getting sweaty!");
                        myRef.child("temp").setValue("false");
                    } else if (dataSnapshot.getKey().equals("fall")) {
                        notifyPopUp(fallAlert, "Grandma has fallen over!");
                        myRef.child("fall").setValue("false");
                    } else if(dataSnapshot.getKey().equals("door0")){
                        //send info to server
                        ///httpPost("https://app.nodecare.me/api/trigger","door0");
                        new httpConnect().execute();
                        myRef.child("door0").setValue("false");
                    } else if(dataSnapshot.getKey().equals("door1")){
                        //httpPost("https://app.nodecare.me/api/trigger","door1");
                        //send info to server
                        new httpConnect().execute();
                        myRef.child("door1").setValue("false");
                    }
                }
            }

            class httpConnect extends AsyncTask<Void, Void, Void>{
                @Override
                protected Void doInBackground(Void... params) {
                    httpPost("https://app.nodecare.me/api/trigger","door0");
                    return null;
                }
            };

            public void httpPost(String urlDesired, String body){
                try {
                    //constants
                    URL url = new URL(urlDesired);
                    String message = body;

                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                    // Create the SSL connection
                    SSLContext sc;
                    sc = SSLContext.getInstance("TLS");
                    sc.init(null, null, new java.security.SecureRandom());
                    conn.setSSLSocketFactory(sc.getSocketFactory());


                    conn.setReadTimeout( 10000 /*milliseconds*/ );
                    conn.setConnectTimeout( 15000 /* milliseconds */ );
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(message.getBytes().length);

                    //open
                    conn.connect();

                    //setup send
                    OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(message.getBytes());

                    //clean up
                    os.flush();
                    os.close();
                    conn.disconnect();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("CHILDEVENTLISTENER",dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("CHILDEVENTLISTENER",dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("CHILDEVENTLISTENER", String.valueOf(databaseError.toException()));
            }
        };
        myRef.addChildEventListener(childEventListener);
    }

    public void decipherData(String message){
        if(message.equals("doora")){
            myRef.child("doora").setValue("true");
        }else if(message.equals("fall")){
            myRef.child("fall").setValue("true");
        }else if(message.equals("pill")){
            myRef.child("pill").setValue("true");
        }else if(message.equals("temp")) {
            myRef.child("temp").setValue("true");
        }else if(message.equals("door1")){
            myRef.child("door1").setValue("true");
        }else if(message.equals("door0")) {
            myRef.child("door0").setValue("true");
        }else{
            //do nothing
        }
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device, UUID uuid) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, uuid);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_SYS){
            if(resultCode == RESULT_OK){
                address = data.getStringExtra("MACADDRESS");
                final BluetoothDevice device = btAdapter.getRemoteDevice(address);
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        establishConnection(device,UUID.fromString(uuidList.get(uuidCount)),bluetoothSocketList.get(uuidCount));
                    }
                };
                thread.start();
            }
        }
    }

    public void establishConnection(BluetoothDevice device, UUID uuid, BluetoothSocket btSocket){
        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        //BluetoothDevice device = btAdapter.getRemoteDevice(macAddress);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device,uuid);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
//            connectedDevices.add(device);
//            bluetoothSocketList.add(btSocket);
//            arrayAdapter.add(device.getName());
            h.obtainMessage(CONNECT_DEVICE,device).sendToTarget();
        } catch (IOException e) {
            try {
                Log.i("", "There was an error while establishing Bluetooth connection. Falling back..");

                Class<?> clazz = btSocket.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};

                btSocket = (BluetoothSocket) m.invoke(btSocket.getRemoteDevice(), params);
                btSocket.connect();
                Log.i(TAG, "Socket Fallback connected");
//                connectedDevices.add(device);
//                bluetoothSocketList.add(btSocket);
//                arrayAdapter.add(device.getName());
                h.obtainMessage(CONNECT_DEVICE,device).sendToTarget();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            } catch (NoSuchMethodException e1) {
                try {
                    btSocket.close();
                    Log.e(TAG,"Socket connection failed. Closing socket.");
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    protected void onDestroy() {
/*        try     {
            if(btSocket.isConnected()) {
                btSocket.close();
            }
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }*/
        super.onDestroy();
    }

    public List<String> retrieveSystem(){
        //database to store system
        List<String> listOfSystem = new ArrayList<>();

        //Do something with it, Firebase or SQL
        return listOfSystem;
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        //Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);		// Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
}