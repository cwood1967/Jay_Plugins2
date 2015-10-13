/*******************************************************************************
 * Copyright (c) 2012 Jay Unruh, Stowers Institute for Medical Research.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import jguis.*;
import ij.io.*;
import java.io.*;

public class import_SKY_jru_v3 implements PlugIn {

	public void run(String arg) {
		try{
			OpenDialog od=new OpenDialog("Open SKY File",arg);
			String dir=od.getDirectory();
			String name=od.getFileName();
			if(name==null){return;}
			InputStream is=new BufferedInputStream(new FileInputStream(dir+File.separator+name));
			SkyPanel_v3 sp=new SkyPanel_v3();
			int nch=5;
			GenericDialog gd2=new GenericDialog("Options");
			gd2.addNumericField("Area Accuracy (percent)",30,0);
			for(int i=0;i<nch;i++){
				gd2.addNumericField("Ch_"+(i+1)+"_Contr_Thresh",0.35,5,15,null);
			}
			//gd2.addNumericField("Contribution Threshold",0.35,5,15,null);
			gd2.addCheckbox("Mouse?",false);
			gd2.addNumericField("Box_Width",150,0);
			gd2.addNumericField("Box_Height",100,0);
			gd2.addCheckbox("Output_Unmixed?",false);
			gd2.showDialog(); if(gd2.wasCanceled()){return;}
			sp.areathresh=(float)gd2.getNextNumber();
			sp.objthresh2=new float[nch];
			for(int i=0;i<nch;i++) sp.objthresh2[i]=(float)gd2.getNextNumber();
			//sp.objthresh=(float)gd2.getNextNumber();
			boolean mouse=gd2.getNextBoolean();
			int bwidth=(int)gd2.getNextNumber();
			int bheight=(int)gd2.getNextNumber();
			boolean outunmixed=gd2.getNextBoolean();
			int[] colorindices={4,1,2,6,3};
			GenericDialog gd3=new GenericDialog("Color Options");
			for(int i=0;i<5;i++) gd3.addChoice("Ch"+(i+1)+" Color",SkyPanel_v3.colornames,SkyPanel_v3.colornames[colorindices[i]]);
			gd3.showDialog(); if(gd3.wasCanceled()) return;
			for(int i=0;i<5;i++) colorindices[i]=gd3.getNextChoiceIndex();
			sp.colorindices=colorindices;
			sp.nch=5;
			sp.dapilast=false;
			sp.cellwidth=bwidth;
			sp.cellheight=bheight;
			sp.init(is,mouse);
			SkyPanel_v3.launch_frame(sp);
			//optionally output the unmixed image
			if(outunmixed){
				ImageStack unstack=jutils.array2stack(sp.unmixed,sp.threshimp.getWidth(),sp.threshimp.getHeight());
				ImagePlus unimp=new ImagePlus("Unmixed",unstack);
				unimp.setOpenAsHyperStack(true);
				unimp.setDimensions(sp.unmixed.length,1,1);
				new CompositeImage(unimp,CompositeImage.COLOR).show();
			}
		}catch(IOException e){
			return;
		}

	}

}
