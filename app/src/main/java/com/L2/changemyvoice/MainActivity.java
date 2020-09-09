package com.L2.changemyvoice;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
//import javax.sound.sampled.AudioFileFormat;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioInputStream;
//import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import android.Manifest;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import com.L2.changemyvoice.constants.AppConstants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends Activity
{
	private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE =22 ;
	private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE =23 ;
	private static final int MY_PERMISSIONS_RECORD_AUDIO_STORAGE = 24;
    private static final int MY_PERMISSIONS_PLAY_RECORD =25 ;
    private static final int INITIAZE_AUDIO_DATA_PERMISION =26 ;
    public static Context Context;
  static AudioTrack audioTrack;
  static File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");;
  static Boolean recording;
  static Spinner spFrequency;
  private ArrayAdapter<String> adapter;
  Animation animRotate;
  private long fileLength;
  private boolean stopped=true;
  ImageView imageView1;
  ImageView playBack;
  ImageView shareWhatsapp;
  short[] inputAudioData;
  ImageView startRec;
  int sample_rate = 0;
  Integer[] freqset = {6050, 8500, 11025, 16000, 22050,
          32000, 37800, 44056, 44100};
  private InterstitialAd interstitial;
 

  void playRecord(){
            playBack.setImageResource(R.drawable.pause_circle);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Initialize();
                    audioTrack.play();
                    stopped=false;
                    audioTrack.setNotificationMarkerPosition(inputAudioData.length);
                    audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                        @Override
                        public void onMarkerReached(AudioTrack track) {
                            playBack.setImageResource(R.drawable.circle_play);
                            stopped = true;
                            audioTrack.stop();
                            audioTrack.release();
                        }

                        @Override
                        public void onPeriodicNotification(AudioTrack track) {

                        }
                    });
                    audioTrack.write(inputAudioData, 0, inputAudioData.length);
                }
            }).start();


            //SaveToWavFile();
			//-----------------------------------------------------------------------------------------
			

}
private void shareWavFile(){
	Initialize();
	SaveToWavFile();
	final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
	shareIntent.setType("audio/wav");
	shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse("file://"+Environment.getExternalStorageDirectory()+"/out10.wav" ));
	startActivity(Intent.createChooser(shareIntent, "Share sound"));
}
private void Initialize(){
    initializeAudioData();
	getSampleRate();
	audioTrack = new AudioTrack(3,
			sample_rate,
			2,
			2,
			inputAudioData.length,
			1);
}

    private void initializeAudioData() {
              if(inputAudioData==null)
                  ReadFromPcmFile();

    }

    private byte[] short2byte(short[] sData,int len) {
	    int shortArrsize = len;
	    byte[] bytes = new byte[shortArrsize * 2];

	    for (int i = 0; i < shortArrsize; i++) {
	        bytes[i * 2] = (byte) (sData[i] & 0x00FF);
	        bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
	        //sData[i] = 0;
	    }
	    return bytes;

	}
  private void getSampleRate(){
	  String str = (String)spFrequency.getSelectedItem();
	    
	    
		
				if (str.equals(getString(R.string.vamp)))
				{
				  sample_rate = 5000;
				}else
				if (str.equals(getString(R.string.slow_motion)))
	            {
	              sample_rate = 6050;
	            }else
	            if (str.equals(getString(R.string.robot)))
	            {
	              sample_rate = 8500;
	            }else
	            if (str.equals(getString(R.string.normal)))
	            {
	              sample_rate = 11025;
	            }else
	            if (str.equals(getString(R.string.chipmunk)))
	            {
	              sample_rate = 16000;
	            }else
	            if (str.equals(getString(R.string.funny)))
	            {
	              sample_rate = 22050;
	            }else
	            if (str.equals(getString(R.string.bee)))
	            {
	              sample_rate = 41000;
	            }else
	            if (str.equals(getString(R.string.elephent)))
	            {
	              sample_rate = 30000;
	            }
  }
  private void SaveToWavFile(){
		  
	  try{
		  byte[] byteData=short2byte(inputAudioData, inputAudioData.length);
      String filename=Environment.getExternalStorageDirectory()+"/out10.wav";
		RandomAccessFile	randomAccessWriter = new RandomAccessFile(filename, "rw");
			
			randomAccessWriter.setLength(0); // Set file length to 0, to prevent unexpected behavior in case the file already existed
			randomAccessWriter.writeBytes("RIFF");
			randomAccessWriter.writeInt(0); // Final file size not known yet, write 0 
			randomAccessWriter.writeBytes("WAVE");
			randomAccessWriter.writeBytes("fmt ");
			randomAccessWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
			randomAccessWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
			randomAccessWriter.writeShort(Short.reverseBytes((short)audioTrack.getChannelCount()));// Number of channels, 1 for mono, 2 for stereo
			randomAccessWriter.writeInt(Integer.reverseBytes(sample_rate)); // Sample rate
			randomAccessWriter.writeInt(Integer.reverseBytes(sample_rate*16*audioTrack.getChannelCount()/8)); // Byte rate, SampleRate*NumberOfChannels*BitsPerSample/8
			randomAccessWriter.writeShort(Short.reverseBytes((short)(16*audioTrack.getChannelCount()/8))); // Block align, NumberOfChannels*BitsPerSample/8
			randomAccessWriter.writeShort(Short.reverseBytes((short)16)); // Bits per sample
			randomAccessWriter.writeBytes("data");
			randomAccessWriter.writeInt(0); // Data chunk size not known yet, write 0
			randomAccessWriter.write(byteData);
			randomAccessWriter.seek(4); // Write size to RIFF header
			randomAccessWriter.writeInt(Integer.reverseBytes(36+byteData.length));
		
			randomAccessWriter.seek(40); // Write size to Subchunk2Size field
			randomAccessWriter.writeInt(Integer.reverseBytes(byteData.length));
		
			randomAccessWriter.close();
	
	
} catch (FileNotFoundException e) {
	e.printStackTrace();
} catch (IOException e) {
	e.printStackTrace();
}
  }
  private void ReadFromPcmFile(){
	  file = new File(Environment.getExternalStorageDirectory(), "test.pcm");
		
      int shortSizeInBytes = Short.SIZE/Byte.SIZE;
		fileLength=file.length();
		int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
		inputAudioData = new short[bufferSizeInBytes];
		
		try {
			InputStream inputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
			
			int j = 0;
			while(dataInputStream.available() > 0){
				inputAudioData[j] = dataInputStream.readShort();
				j++;


			}
			
			dataInputStream.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
  }
  private void startRecord(){

		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "test.pcm"); 
		
		try {
			file.createNewFile();
			
			OutputStream outputStream = new FileOutputStream(file);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
			DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
			
			int minBufferSize = AudioRecord.getMinBufferSize(11025, 2, 2);
			
			short[] audioData = new short[minBufferSize];
			
			AudioRecord audioRecord = new AudioRecord(1, 11025, 2, 2,minBufferSize);
			
			audioRecord.startRecording();
			
			while(recording){
				int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
				for(int i = 0; i < numberOfShort; i++){
					dataOutputStream.writeShort(audioData[i]);
				}
			}
			if (!recording.booleanValue())
	        {
			audioRecord.stop();
			dataOutputStream.close();
			inputAudioData=null;
			initAudioData();
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

  public void onBackPressed()
  {
    super.onBackPressed();
    finish();
  }
  private void createDialog(){
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(R.string.record_your_voice_first)
              .setCancelable(false)
              .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {

                  }
              })
              //Set your icon here
              .setTitle(R.string.warning)
              .setIcon(R.drawable.alert);

      Dialog d =builder.show();//showing the dialog
      int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
      View divider = d.findViewById(dividerId);
      divider.setBackground(getResources().getDrawable(android.R.drawable.divider_horizontal_textfield));
      divider.getLayoutParams().height=1;
      divider.requestLayout();
      int textViewId = d.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
      TextView tv = (TextView) d.findViewById(textViewId);
      tv.setTextColor(getResources().getColor(android.R.color.primary_text_light));
  }

  @SuppressWarnings({ "rawtypes" })
public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_main);
    String[] arrayOfString = new String[8];
    arrayOfString[0] = getApplicationContext().getString(R.string.vamp);
    arrayOfString[1] = getApplicationContext().getString(R.string.slow_motion);
    arrayOfString[2] = getApplicationContext().getString(R.string.robot);
    arrayOfString[3] = getApplicationContext().getString(R.string.normal);
    arrayOfString[4] = getApplicationContext().getString(R.string.chipmunk);
    arrayOfString[5] = getApplicationContext().getString(R.string.funny);
    arrayOfString[6] = getApplicationContext().getString(R.string.bee);
    arrayOfString[7] = getApplicationContext().getString(R.string.elephent);
    startRec = ((ImageView)findViewById(R.id.startrec));
    shareWhatsapp = ((ImageView)findViewById(R.id.shareWhatsapp));
    imageView1 = ((ImageView)findViewById(R.id.imageView3));
    playBack = ((ImageView)findViewById(R.id.playback));
    shareWhatsapp.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

            if (file.exists()) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // only for gingerbread and newer versions

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                        }else
                            shareWavFile();
                    }else
                        shareWavFile();
                }else
                    shareWavFile();
            }
            else
                createDialog();
		}
	});
    startRec.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				// only for gingerbread and newer versions

				if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
					// Permission is not granted
					if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
							Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
						// No explanation needed; request the permission
						ActivityCompat.requestPermissions(MainActivity.this,
								new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
								MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
					}
				}
				if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)						{
					// Permission is not granted
					if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
							Manifest.permission.RECORD_AUDIO)) {
						ActivityCompat.requestPermissions(MainActivity.this,
								new String[]{Manifest.permission.RECORD_AUDIO},
								MY_PERMISSIONS_RECORD_AUDIO_STORAGE);

					} else
						WriteRecordFile();
				} else
					WriteRecordFile();
			}
			else
				WriteRecordFile();
		      }
	});
    playBack.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
            if(!stopped && audioTrack!=null && audioTrack.getState()==AudioTrack.STATE_INITIALIZED &&audioTrack.getPlayState()!=AudioTrack.PLAYSTATE_STOPPED){
                playBack.setImageResource(R.drawable.circle_play);
                stopped=true;
                audioTrack.stop();
                audioTrack.release();
            }else
			if (file.exists()) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // only for gingerbread and newer versions

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_PLAY_RECORD);
                        }else
                            playRecord();
                    }else
                        playRecord();
                }else
                     playRecord();
            }
            else
                createDialog();
		}
	});
    spFrequency = (Spinner)findViewById(R.id.frequency);
    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayOfString);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spFrequency.setAdapter(this.adapter);
    imageView1.setVisibility(View.VISIBLE);
    
 // Prepare the Interstitial Ad
 		interstitial = new InterstitialAd(MainActivity.this);
 		// Insert the Ad Unit ID
 		interstitial.setAdUnitId(AppConstants.interstitial_ad_id);
  
 		//Locate the Banner Ad in activity_main.xml
 		AdView adView = (AdView) this.findViewById(R.id.adView);
  
 		// Request for Ads
 		AdRequest adRequest = new AdRequest.Builder()
 				.build();
  
 		// Load ads into Banner Ads
 		adView.loadAd(adRequest);
  
 		// Load ads into Interstitial Ads
 		interstitial.loadAd(adRequest);
  
 		// Prepare an Interstitial Ad Listener
 		interstitial.setAdListener(new AdListener() {
 			public void onAdLoaded() {
 				// Call displayInterstitial() function
 			}
 		});
 		initAudioData();
  }

	private void WriteRecordFile() {
		Intent localIntent = new Intent(getApplicationContext(), RecordingDailog.class);
		startActivity(localIntent);
		displayInterstitial();

		new Thread(new Runnable()
		{
			public void run()
			{
				recording = Boolean.valueOf(true);
				startRecord();
			}
		})
				.start();
	}
	private void initAudioData(){

        if (file.exists()) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // only for gingerbread and newer versions

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                INITIAZE_AUDIO_DATA_PERMISION);
                    }else
                      new Thread(new Runnable() {
                          @Override
                          public void run() {
                              initializeAudioData();

                          }
                      }).start();
                }else
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            initializeAudioData();

                        }
                    }).start();
            }else
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initializeAudioData();

                    }
                }).start();
        }
    }

	protected void onDestroy()
  {
    super.onDestroy();
    recording = Boolean.valueOf(false);
    if (audioTrack != null)
    {
    	audioTrack.release();
    }
  }
  
  public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
  
	@Override
	public void onResume() 
	{
		super.onResume();
	}

	@Override
	public void onPause() 
	{
		super.onPause();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if(requestCode==MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE && grantResults.length>0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
			if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
				// Permission is not granted
				if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
						Manifest.permission.RECORD_AUDIO)) {
					ActivityCompat.requestPermissions(MainActivity.this,
							new String[]{Manifest.permission.RECORD_AUDIO},
							MY_PERMISSIONS_RECORD_AUDIO_STORAGE);

				} else
					WriteRecordFile();
			}
            else
                WriteRecordFile();
		}else
		if(requestCode==MY_PERMISSIONS_RECORD_AUDIO_STORAGE&& grantResults.length>0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
			WriteRecordFile();
		}
		else
		if(requestCode==MY_PERMISSIONS_READ_EXTERNAL_STORAGE&& grantResults.length>0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
			shareWavFile();
		}
        else
		if(requestCode==MY_PERMISSIONS_PLAY_RECORD&& grantResults.length>0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
			playRecord();
		}
        else
		if(requestCode==INITIAZE_AUDIO_DATA_PERMISION&& grantResults.length>0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initializeAudioData();

                }
            }).start();
        }


	}
}
