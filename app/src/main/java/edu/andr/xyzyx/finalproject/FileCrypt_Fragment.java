package edu.andr.xyzyx.finalproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.spongycastle.crypto.CryptoException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import edu.andr.xyzyx.MyUtil.AES;
import edu.andr.xyzyx.MyUtil.AESFile;
import edu.andr.xyzyx.MyUtil.Base64File;
import edu.andr.xyzyx.MyUtil.ChaCha;
import edu.andr.xyzyx.MyUtil.DESFile;
import edu.andr.xyzyx.MyUtil.FilerHelper;
import edu.andr.xyzyx.MyUtil.GetChachaKeyandIV;
import edu.andr.xyzyx.MyUtil.RSAwithKey;
import edu.andr.xyzyx.MyUtil.RandomString;
import edu.andr.xyzyx.MyUtil.TriDES;
import edu.andr.xyzyx.MyUtil.TriDESFile;
import edu.andr.xyzyx.MyUtil.nDES;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FileCrypt_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FileCrypt_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileCrypt_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String filepath="",fileoutpath="",keypath="";
    private String deskey;
    private View view;
    private String[] li={"a","b","c","d","e"};
    private int pos=0;
    private Spinner selectspinner;
    private Button getfile,enc,dec;
    private EditText endekey,inen,outde;
    private TextView pathtext;
    private Switch sw;
    private boolean isencrypt=false;

//    private OnFragmentInteractionListener mListener;

    public FileCrypt_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FileCrypt_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FileCrypt_Fragment newInstance(String param1, String param2) {
        FileCrypt_Fragment fragment = new FileCrypt_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_file_crypt_fragment, container, false);
        initview();
        selectspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] algorithms = getResources().getStringArray(R.array.algorithms);
                pos=position;
//                Toast.makeText(getContext(), "你点击的是:"+algorithms[pos], Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,3);
            }
        });
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isencrypt=true;
                }else {
                    isencrypt=false;
                }
            }
        });
        enc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key=endekey.getText().toString();
                if(key.length()<=0){
                    Toast.makeText(getContext(),"密钥不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("testx",String.valueOf(isencrypt));
                Log.i("testx",filepath);
                if(isencrypt){
                    if(filepath.equals("")){
                        Toast.makeText(getContext(),"文件不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ENThread enThread=new ENThread();
                    enThread.execute(String.valueOf(pos),key);
                    Toast.makeText(getContext(),"加密开始",Toast.LENGTH_SHORT).show();
                    Log.i("testx","encrypt start");
                }else {
                    String in= inen.getText().toString();
                    ENfun(key,in);
                }

            }
        });
        endekey.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,4);
                return true;
            }
        });
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key=endekey.getText().toString();
                if(key.length()<=0){
                    Toast.makeText(getContext(),"密钥不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
//                Log.i("testx",String.valueOf(isencrypt));
                Log.i("testx",filepath);
                if(isencrypt){
                    if(filepath.equals("")){
                        Toast.makeText(getContext(),"文件不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DEThread deThread=new DEThread();
                    deThread.execute(key);
                }else {
                    String in= inen.getText().toString();
                    DEfun(key,in);
                }
            }
        });
        return view;
//        /storage/emulated/0/666/Duan/file_8858757.png
    }
    private void DEfun(String key,String in){
        switch (pos){
            case 0:
                nDES nDES=new nDES(key);
                try {
                    String out=new String(nDES.decryptString(Base64.decode(in,Base64.DEFAULT)));
                    outde.setText(out);
                } catch (CryptoException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                TriDES triDES=new TriDES();
                String out=new String(triDES.decryptMode(Base64.decode(in,Base64.DEFAULT), key));
                outde.setText(out);
                break;
            case 2:
                AES aes=new AES();
                try {
                    byte[] rawkey = aes.getRawKey(key.getBytes());
                    String out1 = new String(aes.decrypt(rawkey, Base64.decode(in,Base64.DEFAULT)));
                    outde.setText(out1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                ChaCha chaCha=new ChaCha();
                byte[] keys= GetChachaKeyandIV.sha(key).substring(0, 32).getBytes();
                byte[] iv=GetChachaKeyandIV.sha(key).substring(32, 40).getBytes();
                try (InputStream isDec = new ByteArrayInputStream(Base64.decode(in,Base64.DEFAULT));
                     ByteArrayOutputStream osDec = new ByteArrayOutputStream())
                {
                    chaCha.decChaCha(isDec, osDec, keys, iv);
                    byte[] decoded = osDec.toByteArray();
                    outde.setText(new String(decoded));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                outde.setText(new String(Base64.decode(in.getBytes(),Base64.DEFAULT)));
                break;
            default:
                Toast.makeText(getContext(),"不懂",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void ENfun(String key,String in){
        switch (pos){
            case 0:
                if (key.length()!=8){
                    Toast.makeText(getContext(),"密钥非法，DES密钥长度为56位",Toast.LENGTH_SHORT).show();
//                    Snackbar.make(view,"密钥非法,是否随机密钥？",Snackbar.LENGTH_INDEFINITE).setAction("确定", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            deskey= RandomString.getRandomString(8);
//                        }
//                    }) .show();
                }
//                key=deskey;
                nDES nDES=new nDES(key);
                try {
                    String out=new String(Base64.encode(nDES.encryptString(in),Base64.DEFAULT));
                    outde.setText(out);
                } catch (CryptoException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                TriDES triDES=new TriDES();
                String out=new String(Base64.encode(triDES.encryptMode(in.getBytes(), key),Base64.DEFAULT));
                outde.setText(out);
                break;
            case 2:
                AES aes=new AES();
                try {
                    byte[] rawkey = aes.getRawKey(key.getBytes());
                    String out1 = new String(Base64.encode(aes.encrypt(rawkey, in.getBytes()),Base64.DEFAULT));
                    outde.setText(out1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                ChaCha chaCha=new ChaCha();
                byte[] keys= GetChachaKeyandIV.sha(key).substring(0, 32).getBytes();
                byte[] iv=GetChachaKeyandIV.sha(key).substring(32, 40).getBytes();
                try (InputStream isEnc = new ByteArrayInputStream(in.getBytes());
                     ByteArrayOutputStream osEnc = new ByteArrayOutputStream())
                {
                    chaCha.encChaCha(isEnc, osEnc, keys, iv);
                    byte[] encoded = osEnc.toByteArray();
                    outde.setText(new String(Base64.encode(encoded,Base64.DEFAULT)));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                outde.setText(new String(Base64.encode(in.getBytes(),Base64.DEFAULT)));
                break;
            default:
                Toast.makeText(getContext(),"不懂",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 3) {
                Uri uri = data.getData();
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        filepath= Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    pathtext.setText(filepath);
                Log.i("testx",filepath);
            }
            if (requestCode == 4) {
                Uri uri = data.getData();
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    keypath= Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                ReadKey readKey=new ReadKey();
                readKey.execute(keypath);
            }
        }
    }
    private void initview(){
        selectspinner=view.findViewById(R.id.spinner);
        pathtext=view.findViewById(R.id.showoutfile);
        getfile=view.findViewById(R.id.getoutfile);
        enc=view.findViewById(R.id.EncBtn);
        dec=view.findViewById(R.id.decBtu);
        endekey=view.findViewById(R.id.Keyedit);
        inen=view.findViewById(R.id.inputText);
        outde=view.findViewById(R.id.outputText);
        sw=view.findViewById(R.id.switch2);
        Snackbar.make(getActivity().findViewById(android.R.id.content), "使用DES时，注意密钥长度", Snackbar.LENGTH_INDEFINITE)
                .setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }
    class ReadKey extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... strings) {
//            Log.i("testx","read key path: "+strings[0]);
            String[] split= strings[0].split("\\.");
//            Log.i("testx","read key path: "+split[split.length-1]);
            if(!split[split.length-1].equals("mykey")){
                Log.i("testx","read key: "+strings[0]);
                publishProgress("0");
                return "";
            }
            RSAwithKey rsAwithKey=new RSAwithKey();
            String keyy=rsAwithKey.decryptkey("pri",strings[0],getContext(),"source");
            publishProgress("1",keyy);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            switch (values[0]){
                case "0":
                    Toast.makeText(getContext(),"文件类型错误",Toast.LENGTH_SHORT).show();
//                    Snackbar.make(getActivity().findViewById(android.R.id.content), "文件类型错误", Snackbar.LENGTH_SHORT).show();
                    break;
                case "1":
                    endekey.setText(values[1]);
                    break;
            }

        }
    }
    class ENThread extends AsyncTask<String,String,String>{
        private int pos;
        private String key;
        @Override
        protected String doInBackground(String... strings) {
            pos=Integer.parseInt(strings[0]);
            key=strings[1];
            switch (pos){
                case 0:
                    if (key.length()!=8){
                        String skey= RandomString.getRandomString(8);
                        publishProgress("");
                    }
                    try {
                        DESFile desFile=new DESFile(key);
                        desFile.doEncryptFile(filepath,filepath+"."+li[pos]);
                        Log.i("testx","DES files");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    TriDESFile triDESFile=new TriDESFile(filepath,filepath+"."+li[pos],key,getContext());
                    try {
                        triDESFile.encryptfile();
                        Log.i("testx","3DES files");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    AES aesFile=new AES();
                    try {
                        FilerHelper filerHelper=new FilerHelper(getContext());
                        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
                        byte[] buffer=new byte[1024];
                        File file=new File(filepath);
                        FileInputStream is=new FileInputStream(file);
                        int ch;
                        while ((ch = is.read(buffer)) != -1) {
                            bytestream.write(buffer,0,ch);
                        }
                        is.close();
                        byte data[] = bytestream.toByteArray();
                        bytestream.close();
                        byte[] rawkey = aesFile.getRawKey(key.getBytes());
                        byte[] m=aesFile.encrypt(rawkey,data);
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(filepath+"."+li[pos]);
                            fos.write(m);
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.i("testx","AES files");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case 3:
                    ChaCha chaCha=new ChaCha();
                    FilerHelper filerHelper=new FilerHelper(getContext());
                    byte[] keys= GetChachaKeyandIV.sha(key).substring(0, 32).getBytes();
                    byte[] iv=GetChachaKeyandIV.sha(key).substring(32, 40).getBytes();
                    try {
                        chaCha.encChaCha(filerHelper.getFileInputStream(filepath),filerHelper.getFileOutputStream(filepath+"."+li[pos]),keys,iv);
                        Log.i("testx","chacha files");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    Base64File base64File=new Base64File();
                    base64File.encryptfile(getContext(),filepath,filepath+"."+li[pos]);
                    Log.i("testx","base64 files");
                    break;
                default:
                    publishProgress("1");
                    break;
            }
            RSAwithKey rsAwithKey=new RSAwithKey();
            Log.i("testx","key: "+ key);
            rsAwithKey.encryptkey("pub",filepath+"."+"mykey",getContext(),key);
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if(values[0].equals("")) {
                endekey.setText(key);
            }
            if(values[0].equals("1")){
                Toast.makeText(getContext(),"不懂",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
    class DEThread extends AsyncTask<String,String,String>{
        private String key;
        @Override
        protected String doInBackground(String... strings) {
            key=strings[0];
            int dot = filepath.lastIndexOf('.');
            if ((dot >-1) && (dot < (filepath.length()))) {
                fileoutpath= filepath.substring(0, dot);
            }
            String[] split=filepath.split("\\.");
            if(!(split[split.length-1].equals("a")||
                    split[split.length-1].equals("b")||
                    split[split.length-1].equals("c")||
                    split[split.length-1].equals("d")||
                    split[split.length-1].equals("e"))){
                publishProgress("");
                return "";
            }
            String s=split[split.length-1];
            Log.i("testx","alg: "+s);
            switch (s){
                case "a":
                    try {
                        DESFile desFile=new DESFile(key);
                        desFile.doDecryptFile(filepath,fileoutpath);
                        Log.i("testx","DES d files");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "b":
                    TriDESFile triDESFile=new TriDESFile(filepath,fileoutpath,key,getContext());
                    triDESFile.decryptfile();
                    Log.i("testx","3DES d files");
                    break;
                case "c":
                    AES aesFile=new AES();
                    try {
                        FilerHelper filerHelper=new FilerHelper(getContext());
                        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
                        byte[] buffer=new byte[1024];
                        File file=new File(filepath);
                        FileInputStream is=new FileInputStream(file);
                        int ch;
                        while ((ch = is.read(buffer)) != -1) {
                            bytestream.write(buffer,0,ch);
                        }
                        is.close();
                        byte data[] = bytestream.toByteArray();
                        bytestream.close();
                        byte[] rawkey = aesFile.getRawKey(key.getBytes());
                        byte[] m=aesFile.decrypt(rawkey,data);
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(fileoutpath);
                            fos.write(m);
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        };
//                        aesFile.de(filepath,fileoutpath,key,getContext());
                        Log.i("testx","AES d files");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case "d":
                    ChaCha chaCha=new ChaCha();
                    FilerHelper filerHelper=new FilerHelper(getContext());
                    byte[] keys= GetChachaKeyandIV.sha(key).substring(0, 32).getBytes();
                    byte[] iv=GetChachaKeyandIV.sha(key).substring(32, 40).getBytes();
                    try {
                        chaCha.decChaCha(filerHelper.getFileInputStream(filepath),filerHelper.getFileOutputStream(fileoutpath),keys,iv);
                        Log.i("testx","chacha d files");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "e":
                    Base64File base64File=new Base64File();
                    base64File.decryptfile(getContext(),filepath,fileoutpath);
                    Log.i("testx","base64 d files");
                    break;
                default:
                    publishProgress("1");
                    break;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if(values[0].equals("")) {
                Toast.makeText(getContext(), "文件类型错误", Toast.LENGTH_SHORT).show();
            }
            if(values[0].equals("1")){
                Toast.makeText(getContext(),"不懂",Toast.LENGTH_SHORT).show();
            }
        }
    }

// // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

}
