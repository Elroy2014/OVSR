package com.denayer.ovsr;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.renderscript.*;



public class RsScript extends Object {

	private static final int TCP_SERVER_PORT = 64000;
	private static final String TCP_SERVER_IP = "10.123.100.123";
	public Bitmap inBitmap = null;
	public Bitmap outBitmap = null;
	public Context mContext;
	public float saturationValue = 0;
	public ImageButton outputButton;
	public TextView mElapsedTime;
	public MainActivity MmainThread;
	
	
	public RsScript(MainActivity mActiv, ImageButton outImageButton, TextView view) {
		
	   mContext = mActiv;	//needed by renderscript
	   MmainThread = mActiv;	//needed for updating UI components from a subthread
	   outputButton = outImageButton;
	   mElapsedTime = view;
	   
	   
	}
	
	
	public void setInputBitmap(Bitmap in)
	{
		inBitmap = in;
		if(outBitmap==null)
		{
		outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(),inBitmap.getConfig());
		}
	}
	
	public Bitmap getOutputBitmap()
	{
		return outBitmap;
	}
	
	public void RenderScriptEdge()
	{
		if(inBitmap == null)
			return;
	    long startTime = System.nanoTime(); 
		
		Log.i("koen","inside RenderScriptEdge");
		
		float []filter = new float[]{0,-1,0,-1,4,-1,0,-1,0};
        
		
        final RenderScript rs = RenderScript.create(mContext);
        final Allocation input = Allocation.createFromBitmap(rs, inBitmap,Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        
        ScriptC_edgedetection script = new ScriptC_edgedetection(rs);
	    
	    script.set_in(input);
	    script.set_out(output);
	    script.set_script(script);
	    script.set_filterC(filter);
	    script.set_width(inBitmap.getWidth());
	    script.set_height(inBitmap.getHeight());
	    
	    script.invoke_filter();	
	    rs.finish();
	    output.copyTo(outBitmap);
	    
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ms");
		
	}
	
	public void RenderScriptInverse()
	{
		if(inBitmap == null)
			return;
	    long startTime = System.nanoTime(); 
		
		final RenderScript rs = RenderScript.create(mContext);
		Allocation allocIn;
	    allocIn = Allocation.createFromBitmap(rs, inBitmap,
	            Allocation.MipmapControl.MIPMAP_NONE,
	            Allocation.USAGE_SCRIPT);
	    Allocation allocOut = Allocation.createTyped(rs, allocIn.getType());	    
	    ScriptC_inverse script = new ScriptC_inverse(rs);
	    
	    script.set_in(allocIn);
	    script.set_out(allocOut);
	    script.set_script(script);
	    
	    script.invoke_filter();	   
	    //script.forEach_root(allocIn, allocOut);
	    rs.finish();
	    allocOut.copyTo(outBitmap);
	    
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ms");
	    
		
	}	
	
	public void RenderScriptSharpen()
	{
		if(inBitmap == null)
			return;
	    long startTime = System.nanoTime(); 
		
		Log.i("koen","inside RenderScriptSharpen");
		
		float []filter = new float[]{0,-1,0,-1,5,-1,0,-1,0};
        
		
        final RenderScript rs = RenderScript.create(mContext);
        final Allocation input = Allocation.createFromBitmap(rs, inBitmap,Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        
        ScriptC_sharpen script = new ScriptC_sharpen(rs);
	    
	    script.set_in(input);
	    script.set_out(output);
	    script.set_script(script);
	    script.set_filterC(filter);
	    script.set_width(inBitmap.getWidth());
	    script.set_height(inBitmap.getHeight());
	    
	    script.invoke_filter();	
	    rs.finish();
	    
	    output.copyTo(outBitmap);
	    
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ms");
		
	}
	
	public void RenderScriptBlur()
	{
		if(inBitmap == null)
			return;
		long startTime = System.nanoTime(); 
		Log.i("koen","inside RenderScriptBlur");
		
		float []filter = new float[]{1,1,1,1,1,1,1,1,1};
        
		
        final RenderScript rs = RenderScript.create(mContext);
        final Allocation input = Allocation.createFromBitmap(rs, inBitmap,Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        
        ScriptC_blur script = new ScriptC_blur(rs);
	    
	    script.set_in(input);
	    script.set_out(output);
	    script.set_script(script);
	    script.set_filterC(filter);
	    script.set_width(inBitmap.getWidth());
	    script.set_height(inBitmap.getHeight());
	    
	    script.invoke_filter();	
	    rs.finish();
	    output.copyTo(outBitmap);
	    
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ms");
		
	
	}
	
	public void RenderScriptSaturatie()
	{		
		if(inBitmap == null)
			return;		      
        
        final TextView progressView = new TextView(mContext);
		final Resources res = mContext.getResources();
		final SeekBar MySeekBar = new SeekBar(mContext);
		MySeekBar.setMax(200);

		MySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
			   @Override 
			   public void onProgressChanged(SeekBar seekBar, int progress, 
			     boolean fromUser) { 
			    //  Auto-generated method stub 
				   progressView.setText(String.valueOf(progress)); 
			   } 
			   @Override 
			   public void onStartTrackingTouch(SeekBar seekBar) { 
			    //  Auto-generated method stub 
			   } 
			   @Override 
			   public void onStopTrackingTouch(SeekBar seekBar) { 
			    //  Auto-generated method stub 
			   } 
			       }); 
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);        
        builder.setMessage("saturation value")
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {      
                	   saturationValue = MySeekBar.getProgress();  
                	   outBitmap = saturate(inBitmap, saturationValue);
                	   outputButton.setImageBitmap(outBitmap);
                   }
               });
        progressView.setGravity(1 | 0x10);
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
	     LinearLayout ll=new LinearLayout(mContext);
	        ll.setOrientation(LinearLayout.VERTICAL);
	        ll.addView(MySeekBar);
	        ll.addView(progressView);
	        dialog.setView(ll);
        dialog.show(); 
	}
	
	public Bitmap saturate(Bitmap bmIn, float saturation)
	{
		
	    long startTime = System.nanoTime(); 

		final RenderScript rs = RenderScript.create(mContext);	
		
		
	    Bitmap bmOut = Bitmap.createBitmap(bmIn.getWidth(), bmIn.getHeight(),
	            bmIn.getConfig());
	    
	    Allocation allocIn;
	    allocIn = Allocation.createFromBitmap(rs, bmIn,
	            Allocation.MipmapControl.MIPMAP_NONE,
	            Allocation.USAGE_SCRIPT);
	    Allocation allocOut = Allocation.createTyped(rs, allocIn.getType());	    
	    
	    ScriptC_saturation scriptSat = new ScriptC_saturation(rs);	
	    
	    Log.i("koen", "saturation value = " + String.valueOf(saturation));
	    
	    scriptSat.set_in(allocIn);
	    scriptSat.set_out(allocOut);
	    scriptSat.set_script(scriptSat);
	    scriptSat.set_saturation(saturation/100);	    
	    
	    scriptSat.invoke_filter();	   
	    //scriptSat.forEach_root(allocIn, allocOut);
	    rs.finish();
	    allocOut.copyTo(bmOut);
	    
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ms");	    
	   
	    return bmOut;
	}
	
	public void RenderScriptMediaan()
	{
		
		if(inBitmap == null)
			return;
				
		long startTime = System.nanoTime(); 
		Log.i("koen","inside RenderScriptMediaan");        
		
        final RenderScript rs = RenderScript.create(mContext);
        final Allocation input = Allocation.createFromBitmap(rs, inBitmap,Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());        
        
        ScriptC_mediaan script = new ScriptC_mediaan(rs);      
	    
	    script.set_in(input);
	    script.set_out(output);
	    script.set_script(script);
	    
	    script.set_width(inBitmap.getWidth());
	    script.set_height(inBitmap.getHeight());
	    
	    
	    script.invoke_filter();	
	    rs.finish();
	    output.copyTo(outBitmap);
	    
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ms");
		
	}
	
	public void RenderScriptTemplate()
	{
		
		if(inBitmap == null)
			return;
	    long startTime = System.nanoTime(); 
		
		final RenderScript rs = RenderScript.create(mContext);
		Allocation allocIn;
	    allocIn = Allocation.createFromBitmap(rs, inBitmap,
	            Allocation.MipmapControl.MIPMAP_NONE,
	            Allocation.USAGE_SCRIPT);
	    Allocation allocOut = Allocation.createTyped(rs, allocIn.getType());
	    
	    MyResources myRes = new MyResources(mContext.getResources().getAssets(), mContext.getResources().getDisplayMetrics(), mContext.getResources().getConfiguration());
	    myRes.setMyContext(mContext);
	    Resources hackedResources = myRes;	    
	    
	    ScriptC_template script = new ScriptC_template(rs,hackedResources,rs.getApplicationContext().getResources().getIdentifier(
                "template", "raw",rs.getApplicationContext().getPackageName()));	    
	    
	    
	    script.set_in(allocIn);
	    script.set_out(allocOut);
	    script.set_script(script);
	    
	    script.invoke_filter();	   	  
	    rs.finish();
	    allocOut.copyTo(outBitmap);
	    
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ms");
		
	}
	
	public String getTemplate()
	{
		String template = null;
		try {
			InputStream in = mContext.getAssets().open("templateRenderscript.txt");
			InputStreamReader inputStreamReader = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String receiveString = "";
			StringBuilder stringBuilder = new StringBuilder();

			while ( (receiveString = bufferedReader.readLine()) != null ) {
				stringBuilder.append(receiveString).append("\n");
			}
			in.close();
			template = stringBuilder.toString();
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
			e.printStackTrace();
		}
		
		
		return template;
	}
	public void codeFromFile(String code)
	{
			
	}	
	
	
	
	
}









