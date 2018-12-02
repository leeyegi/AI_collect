package com.example.yegilee.ai_collect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity  extends BlunoLibrary {
	private Button buttonScan;
	private TextView textState;
	private EditText editLableNum;
	private Button buttonStart;
	private Button buttonEnd;
	private TextView textSerialReceived;


	String labelValue;
	boolean flagSave=false;

	final static String foldername = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/";
	final static String filename = "logfile.txt";
	final static String filename2 = "tagfile.txt";
	int globalIdx=0;



	FileOutputStream fos;
	FileOutputStream fos2;
	BufferedWriter fw;
	BufferedWriter fw2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onCreateProcess();                                          //onCreate Process by BlunoLibrary

		serialBegin(115200);                                       //set the Uart Baudrate on BLE chip to 115200



		buttonScan=(Button)findViewById(R.id.buttonScan);
		//textAdress=(TextView)findViewById(R.id.textAddress);
		textState=(TextView)findViewById(R.id.textState);
		editLableNum=(EditText)findViewById(R.id.editLableNum);
		buttonStart=(Button)findViewById(R.id.buttonStart);
		buttonEnd=(Button)findViewById(R.id.buttonEnd);
		textSerialReceived=(TextView)findViewById(R.id.textSerialReceived);

		buttonScan.setOnClickListener(buttonScanListener);
		buttonStart.setOnClickListener(buttonStartListener);
		buttonEnd.setOnClickListener(buttonEndListener);

	}

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();                                          //onResume Process by BlunoLibrary
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);               //onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		onPauseProcess();                                          //onPause Process by BlunoLibrary
	}

	protected void onStop() {
		super.onStop();
		onStopProcess();                                          //onStop Process by BlunoLibrary
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onDestroyProcess();                                          //onDestroy Process by BlunoLibrary
	}



	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {                                 //Four connection state
			case isConnected:
				textState.setText("Connected");
				break;
			case isConnecting:
				textState.setText("Connecting");
				break;
         /*
      case isToScan:
         textState.setText("Scan");
         break;*/
			case isScanning:
				textState.setText("Scanning");
				break;
			case isDisconnecting:
				textState.setText("isDisconnecting");
				break;
			default:
				break;
		}
		if(textState.getText().equals("Connected")){
			buttonScan.setEnabled(false);
		}else{
			buttonScan.setEnabled(true);

		}
	}

	Queue<String> queueStr=new LinkedList<String>();
	Queue<String> queueSave=new LinkedList<String>();
	StringBuilder  bufferStr=new StringBuilder ();
	int index1=0;
	int index2=0;
	StringBuilder tmp;

	@Override
	public void onSerialReceived(String theString) {                     //Once connection data received, this function will be called
		// TODO Auto-generated method stub
		//textSerialReceived.append(theString);                     //append the text into the EditText

		bufferStr.append(theString);

		//textSerialReceived.append(String.valueOf(bufferStr.length())+"\n");


		if(bufferStr.length()>25 && bufferStr.lastIndexOf("\n")>1) {
			//Thread threadOne = new Thread1();
			//threadOne.start();
			index2 = bufferStr.lastIndexOf("\n");

			//if(bufferStr.lastIndexOf("\n")<45) {

				if(flagSave==true) {
					//Log.e("index length", String.valueOf(index1) + " " + String.valueOf(index2));
					//textSerialReceived.append(String.valueOf(bufferStr));
					queueSave.offer(bufferStr.substring(index1, index2));
					tmp  = bufferStr.delete(0, index2);

				}else{
					//textSerialReceived.append(String.valueOf(bufferStr));
					queueStr.offer(bufferStr.substring(index1, index2));
					tmp  = bufferStr.delete(0, index2);

				}
			//tmp  = bufferStr.delete(0,bufferStr.length());
			//}else{
				//drop=bufferStr.substring(index1, index2);
				//tmp  = bufferStr.delete(0, index2);
				//textSerialReceived.append("drop data occured\n");
			//}
			index1 = 1;
			if(queueStr.size()%10==0 && queueSave.size()%10==0) {
			textSerialReceived.append("queueStr length" + String.valueOf(queueStr.size()) + "\n");
			textSerialReceived.append("queueSave length" + String.valueOf(queueSave.size()) + "\n");
			}


			//tmp  = bufferStr.delete(0,bufferStr.length());
			//Log.e("tmp", String.valueOf(tmp));
		}



		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
		((ScrollView)textSerialReceived.getParent()).fullScroll(View.FOCUS_DOWN);
	}
	//===================================================================

	View.OnClickListener buttonScanListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			buttonScanOnClickProcess();                              //Alert Dialog for selecting the BLE device

		}
	};

	View.OnClickListener buttonStartListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			flagSave=true;
			//Log.e("button status", "start");
			buttonStart.setEnabled(false);
			buttonEnd.setEnabled(true);

			labelValue= String.valueOf(editLableNum.getText());
			textSerialReceived.append(labelValue+"번 수집시작"+"\n");

			queueStr.clear();
			//bufferStr.delete(0,bufferStr.length());

		}
	};

	View.OnClickListener buttonEndListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			flagSave=false;
			//Log.e("button status", "end");
			buttonStart.setEnabled(true);
			buttonEnd.setEnabled(false);

			textSerialReceived.append(labelValue+"번 수집종료"+"\n");
			editLableNum.setText("");

			saveFile();
		}
	};

	String buf;
	String[] bufSize;
	public void saveFile() {
		try {
			textSerialReceived.append(labelValue+"번 클래스 저장중\n");

			Log.e("saveFile()","파일을 생성하여 저장합니다.\n");
			File dir = new File (foldername);

			if(!dir.exists()){
				dir.mkdir();
			}

			fos = new FileOutputStream(foldername+"/"+filename, true);
			fos2 = new FileOutputStream(foldername+"/"+filename2, true);

			fw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			fw2 = new BufferedWriter(new OutputStreamWriter(fos2,"UTF-8"));

			int idx=0;
			//globalIdx2+=idx;
			for(int i=0;i<queueSave.size()-1;){
				buf=String.valueOf(queueSave.poll());
				if(buf.split(" ").length==6) {

					fw.write(String.valueOf(idx++));
					fw.write(" ");

					//fw.write(String.valueOf(queueSave.poll()));       fw.write(" ");
					//fw.write(String.valueOf(queueSave.poll()));        fw.write(" ");
					//fw.write(String.valueOf(queueSave.poll()));          fw.write(" ");
					//fw.write(String.valueOf(queueSave.poll()));        fw.write(" ");
					//fw.write(String.valueOf(queueSave.poll()));        fw.write(" ");
					fw.write(buf);
					fw.write("\r\n");

				}
			}
			Log.e("queueSave 남은 수",String.valueOf(queueSave.size()));
			queueSave.clear();
			Log.e("queueSave 남은 수",String.valueOf(queueSave.size()));

			fw2.write(labelValue);       fw2.write(" ");
			fw2.write(String.valueOf(globalIdx));       fw2.write(" ");
			globalIdx+=idx;
			fw2.write(String.valueOf(globalIdx-1));       fw2.write("\r\n");
			Log.e("saveFile()","저장완료");
			textSerialReceived.append(labelValue+"번 클래스 저장완료\n");

			fw.close();
			//fw.flush();
			fw2.close();
			//fw2.flush();
		}catch (Exception e) {
			e.printStackTrace() ;
		}
	}

}