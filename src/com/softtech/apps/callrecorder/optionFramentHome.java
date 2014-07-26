package com.softtech.apps.callrecorder;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "NewApi", "ValidFragment" })
public class optionFramentHome extends ListFragment implements OnItemClickListener {
	
	private CustomListVoiceAdapter voiceAdapter;
	private Context context;
	
	MediaPlayer mediaPlayer;
	Button start,pause,stop;  
	Dialog dialog;
	
	private SeekBar volumeControl = null;
	Handler seekHandler = new Handler();
	int progressChanged = 0;
	
	@SuppressLint("ValidFragment")
	public optionFramentHome(Context context) {
		super();
		this.context = context;
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater .inflate(R.layout.home, container, false); 
		
        return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		voiceAdapter = new CustomListVoiceAdapter(context);
        setListAdapter(voiceAdapter);
        getListView().setOnItemClickListener(this);
        registerForContextMenu(getListView());
        
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
		RowVoiceRecorded itemClicked = CustomListVoiceAdapter.rowVoiceRecorded.get(position);
		final String path = itemClicked.getmPath();
		 
		mediaPlayer = new MediaPlayer();
		
		Log.d("PATH", "Path = "+path);
		dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.media_player);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true); 
        dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				volumeControl = null;
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
        });
        
        
        try {
			mediaPlayer.setDataSource(path);
		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			mediaPlayer.prepare();
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		start=(Button)dialog.findViewById(R.id.btnStart);  
        pause=(Button)dialog.findViewById(R.id.btnPause);  
        stop=(Button)dialog.findViewById(R.id.btnStop); 
        
        // Seek bar volume control
        volumeControl = (SeekBar) dialog.findViewById(R.id.seekBar);
		volumeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
 
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressChanged = progress;
			}
 
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
 
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
		});
		
        start.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	mediaPlayer.start();  
            	volumeControl.setMax(mediaPlayer.getDuration());
            	seekUpdation();
            }  
        });  
        pause.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	mediaPlayer.pause();  
            }  
        });  
        stop.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	dialog.dismiss();
            }  
        });  
        
        dialog.show();
        
 

        
	}
	
	public void seekUpdation() {
		if(volumeControl != null && mediaPlayer != null){
			volumeControl.setProgress(mediaPlayer.getCurrentPosition());
			seekHandler.postDelayed(run, 1000);
		}
		
	}


    Runnable run = new Runnable() {

 		@Override
 		public void run() {
 			seekUpdation();
 		}
 	};

 	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		 MenuInflater inflater = getActivity().getMenuInflater();
		 inflater.inflate(R.menu.dialog, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		  switch (item.getItemId()) {
		    case R.id.action_delete:
		    	Toast.makeText(context,
						 "Delete",
					      Toast.LENGTH_LONG).show();
		    	break;
		    case R.id.action_share:
		    	Toast.makeText(context,
						 "Share",
					      Toast.LENGTH_LONG).show();
		    	break;
		    case R.id.action_backup:
		    	Toast.makeText(context,
						 "Backup",
					      Toast.LENGTH_LONG).show();
		    	break;
		    case R.id.action_favorite:
		    	Toast.makeText(context,
						 "Favorite",
					      Toast.LENGTH_LONG).show();
		    	break;
		    default:
		    	break;
		 }
		  
		return super.onContextItemSelected(item);
	}

}
