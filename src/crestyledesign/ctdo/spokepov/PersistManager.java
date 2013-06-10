package crestyledesign.ctdo.spokepov;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.swing.JOptionPane;

public class PersistManager {
	private static int dataFileVersion = 1337;
	
	
	public static boolean saveFile(String filename, FileDataObject obj) {
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(dataFileVersion);
			oos.writeInt(obj.getAngularSteps());
			oos.writeInt(obj.getLedCount());
			oos.writeObject(obj.getData());
			fos.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static boolean loadFile(String filename, FileDataObject obj) {
		try {
			FileInputStream fos = new FileInputStream(filename);
			ObjectInputStream oos = new ObjectInputStream(fos);
			int version = oos.readInt();
			if( version == dataFileVersion ) {
				obj.setAngularSteps(oos.readInt());
				obj.setLedCount(oos.readInt());
				// TODO: check if angularSteps*ledCount*sizeOf(DotDataEntry) = sizeof(data)
				obj.setData((DotDataEntry[][]) oos.readObject());
			}
			else {
				JOptionPane.showMessageDialog(null, "Wrong file version");
				return false;
			}
			fos.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static boolean saveCHeaderFile(String filename, FileDataObject obj) {
		StringBuilder sbRed = new StringBuilder();
        StringBuilder sbGreen = new StringBuilder();
        StringBuilder sbBlue = new StringBuilder();

        FileWriter sw;
		try {
			sw = new FileWriter(filename);
		
	        sw.append("/* generated by spokepovmaker (version " + dataFileVersion + ") " + (new Date()) + " */\r\n");
	        sw.append("const int8_t wheel_divisions = " + obj.getAngularSteps() + ";\r\n");
	        sbRed.append("\r\nuint8_t red[" + obj.getAngularSteps() + "][" + obj.getLedCount() + "] PROGMEM = {\r\n");
	        sbGreen.append("\r\nuint8_t green[" + obj.getAngularSteps() + "][" + obj.getLedCount() + "] PROGMEM = {\r\n");
	        sbBlue.append("\r\nuint8_t blue[" + obj.getAngularSteps() + "][" + obj.getLedCount() + "] PROGMEM = {\r\n");
	
	        for (int i = 0; i < obj.getAngularSteps(); i++)
	        {
	            sbRed.append("  {");
	            sbGreen.append("  {");
	            sbBlue.append("  {");
	
	            for (int j = 0; j < obj.getLedCount(); j++)
	            {
	                Color c;
	                if(obj.getData()[i][j] == null) 
	                    c = Color.black;
	                else
	                    c = obj.getData()[i][j].getColor();
	
	                sbRed.append(c.getRed());
	                sbGreen.append(c.getGreen());
	                sbBlue.append(c.getBlue());
	
	                if (j < obj.getLedCount() - 1)
	                {
	                    sbRed.append(",");
	                    sbGreen.append(",");
	                    sbBlue.append(",");
	                }
	            }
	
	            sbRed.append("}");
	            sbGreen.append("}");
	            sbBlue.append("}");
	
	            if (i < obj.getAngularSteps() - 1)
	            {
	                sbRed.append(",\r\n");
	                sbGreen.append(",\r\n");
	                sbBlue.append(",\r\n");
	            }
	        }
	
	        sbRed.append("\r\n};\r\n");
	        sbGreen.append("\r\n};\r\n");
	        sbBlue.append("\r\n};\r\n");
	

	        sw.append(sbRed);
	        sw.append(sbGreen);
	        sw.append(sbBlue);
	        sw.append("\r\n/* PIMML!!!\r\n  Visit Chaostreff Dortmund (www.ctdo.de) */");
	        sw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}
}
