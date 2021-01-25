/* 
 *  Copyright 2012 Samuel Taylor
 * 
 *  This file is part of darkFunction Editor
 *
 *  darkFunction Editor is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  darkFunction Editor is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with darkFunction Editor.  If not, see <http://www.gnu.org/licenses/>.
 */

package dfEditor.io;

import dfEditor.*;
import java.io.File;
import dfEditor.animation.*;
import com.generationjava.io.xml.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
/**
 * Interface between the editors and the file system.
 * Also encapsulates the format.
 *
 * @author s4m20
 */
public class AnimationSetWriter
{
    public AnimationSetWriter()
    {

    }
    
    public void createAnimationSet(File aSaveFile, String aSpritesheetName, ArrayList<Animation> aAnimList) {
    	createAnimationSetImpl(aSaveFile, aSpritesheetName, aAnimList, false);
    	// Don't write the animation file for weapons if there are no weapons in the animation
    	for (Animation anim : aAnimList)
    	{
    		if (hasWeaponOrSwoosh(anim)) {
    			createAnimationSetImpl(new File(aSaveFile.getAbsolutePath().replace(".anim", "") + "-weapon.anim"), aSpritesheetName, aAnimList, true);
    			break;
    		}
    	}
    }

    public void createAnimationSetImpl(File aSaveFile, String aSpritesheetName, ArrayList<Animation> aAnimList, boolean exportWeapon)
    {
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(aSaveFile));

            PrettyPrinterXmlWriter xmlwriter = new PrettyPrinterXmlWriter(new SimpleXmlWriter(out));
            
            xmlwriter.writeXmlVersion();
            String comment = "Generated by darkFunction Editor (www.darkfunction.com)";
            xmlwriter.writeComment(comment);

            xmlwriter.writeEntity("animations");
            xmlwriter.writeAttribute("spriteSheet", aSpritesheetName);
            xmlwriter.writeAttribute("ver", "1.2");

            for (int i=0; i<aAnimList.size(); ++i)
            {
                writeAnimToXml(xmlwriter, aAnimList.get(i), exportWeapon);
            }

            xmlwriter.endEntity();
            xmlwriter.close();
            out.close();
        }
        catch (IOException e)
        {
        	e.printStackTrace();
        }
    }

    private boolean hasWeaponOrSwoosh(Animation aAnimation) {
    	AnimationCell cell = aAnimation.getCurrentCell();
        while(cell !=  null)
        {
           ArrayList<GraphicObject> graphicList = cell.getGraphicList();
           for (int i=0; i<graphicList.size(); ++i) {
               SpriteGraphic graphic = (SpriteGraphic)graphicList.get(i);
               CustomNode node = cell.nodeForGraphic(graphic);
               if (node.getFullPathName().equalsIgnoreCase("/Weapon") || node.getFullPathName().equalsIgnoreCase("/Swoosh"))
            	   return true;
           }

           cell = aAnimation.getNextCell();
        }
        return false;
    }
    
    private void writeAnimToXml(XmlWriter aXmlWriter, Animation aAnimation, boolean exportWeapon) throws IOException
    {
         aXmlWriter.writeEntity("anim");
         aXmlWriter.writeAttribute("name", aAnimation.getName());
         aXmlWriter.writeAttribute("loops", aAnimation.getLoops());

         int backupIndex = aAnimation.getCurrentCellIndex();
         aAnimation.setCurrentCellIndex(0);
         AnimationCell cell = aAnimation.getCurrentCell();
         while(cell !=  null)
         {
            aXmlWriter.writeEntity("cell");
            aXmlWriter.writeAttribute("index", aAnimation.getCurrentCellIndex());
            aXmlWriter.writeAttribute("delay", cell.getDelay());

            ArrayList<GraphicObject> graphicList = cell.getGraphicList();
            for (int i=0; i<graphicList.size(); ++i) {
                SpriteGraphic graphic = (SpriteGraphic)graphicList.get(i);
                CustomNode node = cell.nodeForGraphic(graphic);
                if (exportWeapon == (node.getFullPathName().equalsIgnoreCase("/Weapon") || node.getFullPathName().equalsIgnoreCase("/Swoosh")))
                {
	                aXmlWriter.writeEntity("spr");
	                aXmlWriter.writeAttribute("name", node.getFullPathName());
	                aXmlWriter.writeAttribute("x", graphic.getRect().x + graphic.getRect().width/2);
	                aXmlWriter.writeAttribute("y", graphic.getRect().y + graphic.getRect().height/2);                                
	                aXmlWriter.writeAttribute("z", cell.zOrderOfGraphic(graphic));                                
	                
	                if (graphic.getAngle() != 0)
	                    aXmlWriter.writeAttribute("angle", graphic.getAngle());
	                if (graphic.isFlippedV())
	                    aXmlWriter.writeAttribute("flipV", graphic.isFlippedV() ? 1 : 0);
	                if (graphic.isFlippedH())
	                    aXmlWriter.writeAttribute("flipH", graphic.isFlippedH() ? 1 : 0);
	                
	                aXmlWriter.endEntity();
                }
            }

            aXmlWriter.endEntity();

            cell = aAnimation.getNextCell();
         }

         aXmlWriter.endEntity();
    }

    

}
