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
import javax.swing.JTree;
import com.generationjava.io.xml.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;

/**
 * Interface between the editors and the file system.
 * Also encapsulates the format.
 *
 * @author s4m20
 */
public class SpritesheetWriter
{
    public SpritesheetWriter()
    {

    }

    public void createSpriteSheet(File aSaveFile, String aImageName, JTree aTree, int aImageWidth, int aImageHeight) throws IOException
    {    
        BufferedWriter out = new BufferedWriter(new FileWriter(aSaveFile));

        PrettyPrinterXmlWriter xmlwriter = new PrettyPrinterXmlWriter(new SimpleXmlWriter(out));

        xmlwriter.writeXmlVersion();
        String comment = "Generated by darkFunction Editor (www.darkfunction.com)";
        xmlwriter.writeComment(comment);

        xmlwriter.writeEntity("img");
        xmlwriter.writeAttribute("name", aImageName);
        xmlwriter.writeAttribute("w", aImageWidth);
        xmlwriter.writeAttribute("h", aImageHeight);
            xmlwriter.writeEntity("definitions");
                writeNodeToXml(xmlwriter, (CustomNode)aTree.getModel().getRoot());
            xmlwriter.endEntity();
        xmlwriter.endEntity();

        xmlwriter.close();
        //System.err.println(out.toString());
        out.close();
    }

    private void writeNodeToXml(XmlWriter xmlwriter, CustomNode node) throws IOException
    {
        if (node.isLeaf())
        {               
            java.awt.Rectangle r = ((GraphicObject)node.getCustomObject()).getRect();

            xmlwriter.writeEntity("spr");
            xmlwriter.writeAttribute("name", node.getUserObject()); 
            xmlwriter.writeAttribute("x", r.x);
            xmlwriter.writeAttribute("y", r.y);
            xmlwriter.writeAttribute("w", r.width);
            xmlwriter.writeAttribute("h", r.height);
            xmlwriter.endEntity();
        }
        else
        {           
            xmlwriter.writeEntity("dir");
            xmlwriter.writeAttribute("name", node.getUserObject());

            for (int i=0; i<node.getChildCount(); ++i)
            {
                writeNodeToXml(xmlwriter, (CustomNode)node.getChildAt(i));
            }

            xmlwriter.endEntity();
        }
    }

}
