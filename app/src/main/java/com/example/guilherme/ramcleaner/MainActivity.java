package com.example.guilherme.ramcleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    TextView processView;
    private boolean re = true;

    int valor=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processView = (TextView) findViewById(R.id.process);
        showPro();

        numeroprocessos();

        TextView tx = (TextView) findViewById(R.id.openprocess);
        tx.setText("Processos Ativos:"+valor);

        TextView totalMemView = (TextView) findViewById(R.id.total_mem);
        TextView currentMem = (TextView) findViewById(R.id.current_mem);
        totalMemView.setText("" + getTotalMemory(this));
        currentMem.setText("" + getAvailMemory(this));
    }

    private long getAvailMemory(Context context) {
        // Obter o tamanho da memória atualmente disponível android
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; // A memória disponível do sistema atual
        // return Formatter.formatFileSize(context, mi.availMem);// O tamanho da memória vai ficar normalizada
        return mi.availMem / (1024 * 1024);
    }

    private long getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";//Arquivo de informações de memória do sistema
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();//A primeira linha lê meminfo，Tamanho total da memória do sistema


            arrayOfString = str2.split("\\s+");

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// Obter memória total do sistema，A unidade é KB，1024 Por convertido para Byte
            localBufferedReader.close();

        } catch (IOException e) {

        }
        // return Formatter.formatFileSize(context, initial_memory);//
        // Byte convertido em KB ou MB，Tamanho normalização de memória
        return initial_memory / (1024 * 1024);
    }

    public void clear(View v) {
        ActivityManager activityManger = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
        if (list != null)
            for (int i = 0; i < list.size(); i++) {
                ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
                String[] pkgList = apinfo.pkgList;
                if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
                    Process.killProcess(apinfo.pid);
                    for (int j = 0; j < pkgList.length; j++) {
                        // 2.2Estes são desatualizados,Por favor, ser substituído por killBackgroundProcesses
                        activityManger.killBackgroundProcesses(pkgList[j]);
                        activityManger.restartPackage(pkgList[j]);
                    }
                }
            }
        int aux = valor;
        list = activityManger.getRunningAppProcesses();
        valor = list.size();
        int proc = aux - valor;
        Toast.makeText(MainActivity.this, proc+" Processos Eliminados", Toast.LENGTH_SHORT).show();
        atualizardados();
        /*new Handler().postDelayed(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, getString(R.string.quit), Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
        }, 7000);*/
    }

    private void showPro() {
        final ActivityManager activityManger = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                processView.setText(getString(R.string.wait));
            }

            @Override
            protected String doInBackground(Void... params) {
                List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
                StringBuilder sb = new StringBuilder("");
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
                        sb.append(i + 1 + "==pid:" + apinfo.pid
                                + "#processName:" + apinfo.processName
                                + "#importance:" + apinfo.importance
                                + "\n--------------------------------\n");
                    }



                }
                //TextView titleTextView = (TextView) findViewById(R.id.title);
                //titleTextView.setText(titleTextView.getText() + " " + list.size());
                //sb.append(getString(R.string.xiaodong));
                return sb.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                processView.setText(result);
                if (re) {
                    re = false;
                    //Toast.makeText(MainActivity.this, getString(R.string.des1), Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            System.out.println("valor");
                            //atualizardados();
                            //showPro();
                            //clear(null);
                        }
                    }, 2000);
                }
            }
        }.execute();
    }

    public void atualizardados(){
        showPro();

        TextView tx = (TextView) findViewById(R.id.openprocess);
        tx.setText("Processos Ativos:"+valor);

        TextView totalMemView = (TextView) findViewById(R.id.total_mem);
        TextView currentMem = (TextView) findViewById(R.id.current_mem);
        totalMemView.setText("" + getTotalMemory(this));
        currentMem.setText("" + getAvailMemory(this));
    }

    public void numeroprocessos(){
        ActivityManager activityManger = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
        valor = list.size();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}
