/*
Copyright (c) 2021, Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/fxsvgimage
 */
package org.girod.javafx.svgimage.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.girod.javafx.svgimage.xml.FilterSpec.FESpecularLighting;
import static org.girod.javafx.svgimage.xml.SVGTags.DX;
import static org.girod.javafx.svgimage.xml.SVGTags.DY;
import static org.girod.javafx.svgimage.xml.SVGTags.FLOOD_COLOR;
import static org.girod.javafx.svgimage.xml.SVGTags.FLOOD_OPACITY;
import static org.girod.javafx.svgimage.xml.SVGTags.STD_DEVIATION;

/**
 * The shape builder.
 *
 * @version 0.4
 */
public class SVGShapeBuilder implements SVGTags {
   private SVGShapeBuilder() {
   }

   /**
    * Build a "rect" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildRect(XMLNode xmlNode, Viewport viewport) {
      double x = 0.0;
      double y = 0.0;

      if (xmlNode.hasAttribute(X)) {
         x = xmlNode.getAttributeValueAsDouble(X, true, viewport);
      }
      if (xmlNode.hasAttribute(Y)) {
         y = xmlNode.getAttributeValueAsDouble(Y, false, viewport);
      }
      double width = xmlNode.getAttributeValueAsDouble(WIDTH, true, viewport, 0);
      double height = xmlNode.getAttributeValueAsDouble(HEIGHT, false, viewport, 0);
      Rectangle rect = new Rectangle(x, y, width, height);
      return rect;
   }

   /**
    * Build a "circle" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildCircle(XMLNode xmlNode, Viewport viewport) {
      double cx = xmlNode.getAttributeValueAsDouble(CX, true, viewport, 0);
      double cy = xmlNode.getAttributeValueAsDouble(CY, false, viewport, 0);
      double r = xmlNode.getAttributeValueAsDouble(R, true, viewport, 0);
      Circle circle = new Circle(cx, cy, r);

      return circle;
   }

   /**
    * Build a "text" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the Text
    */
   public static Text buildText(XMLNode xmlNode, Viewport viewport) {
      Font font = null;
      if (xmlNode.hasAttribute(FONT_FAMILY) && xmlNode.hasAttribute(FONT_SIZE)) {
         font = Font.font(xmlNode.getAttributeValue(FONT_FAMILY).replace("'", ""),
            xmlNode.getAttributeValueAsDouble(FONT_SIZE));
      }

      String cdata = xmlNode.getCDATA();
      if (cdata != null) {
         double x = xmlNode.getAttributeValueAsDouble(X, true, viewport, 0);
         double y = xmlNode.getAttributeValueAsDouble(Y, false, viewport, 0);
         Text text = new Text(x, y, cdata);
         if (font != null) {
            text.setFont(font);
         }

         return text;
      } else {
         return null;
      }
   }

   /**
    * Build an "image" node.
    *
    * @param xmlNode the node
    * @param url the reference url
    * @param viewport the viewport
    * @return the ImageView
    */
   public static ImageView buildImage(XMLNode xmlNode, URL url, Viewport viewport) {
      double width = xmlNode.getAttributeValueAsDouble(WIDTH, true, viewport, 0);
      double height = xmlNode.getAttributeValueAsDouble(HEIGHT, false, viewport, 0);
      double x = xmlNode.getAttributeValueAsDouble(X, true, viewport, 0);
      double y = xmlNode.getAttributeValueAsDouble(Y, false, viewport, 0);
      String hrefAttribute = xmlNode.getAttributeValue(HREF);

      URL imageUrl = null;
      try {
         imageUrl = new URL(hrefAttribute);
      } catch (MalformedURLException ex) {
         try {
            imageUrl = new URL(url, hrefAttribute);
         } catch (MalformedURLException ex1) {
         }
      }
      if (imageUrl != null) {
         Image image = new Image(imageUrl.toString(), width, height, true, true);
         ImageView view = new ImageView(image);
         view.setX(x);
         view.setY(y);
         return view;
      }

      return null;
   }

   /**
    * Build an "ellipse" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildEllipse(XMLNode xmlNode, Viewport viewport) {
      Ellipse ellipse = new Ellipse(xmlNode.getAttributeValueAsDouble(CX, true, viewport, 0),
         xmlNode.getAttributeValueAsDouble(CY, false, viewport, 0),
         xmlNode.getAttributeValueAsDouble(RX, true, viewport, 0),
         xmlNode.getAttributeValueAsDouble(RY, false, viewport, 0));

      return ellipse;
   }

   /**
    * Build an "path" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildPath(XMLNode xmlNode, Viewport viewport) {
      SVGPath path = new SVGPath();
      String content = xmlNode.getAttributeValue(D);
      content = content.replace('−', '-');
      path.setContent(content);

      return path;
   }

   /**
    * Build a "polygon" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildPolygon(XMLNode xmlNode, Viewport viewport) {
      String pointsAttribute = xmlNode.getAttributeValue("points");
      Polygon polygon = new Polygon();

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");
      while (tokenizer.hasMoreTokens()) {
         String point = tokenizer.nextToken();

         StringTokenizer tokenizer2 = new StringTokenizer(point, ",");
         Double x = ParserUtils.parseDoubleProtected(tokenizer2.nextToken(), true, viewport);
         Double y = ParserUtils.parseDoubleProtected(tokenizer2.nextToken(), false, viewport);

         polygon.getPoints().add(x);
         polygon.getPoints().add(y);
      }

      return polygon;
   }

   /**
    * Build a "line" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildLine(XMLNode xmlNode, Viewport viewport) {
      if (xmlNode.hasAttribute(X1) && xmlNode.hasAttribute(Y1) && xmlNode.hasAttribute(X2) && xmlNode.hasAttribute(Y2)) {
         double x1 = xmlNode.getAttributeValueAsDouble(X1, true, viewport);
         double y1 = xmlNode.getAttributeValueAsDouble(Y1, false, viewport);
         double x2 = xmlNode.getAttributeValueAsDouble(X2, true, viewport);
         double y2 = xmlNode.getAttributeValueAsDouble(Y2, false, viewport);

         return new Line(x1, y1, x2, y2);
      } else {
         return null;
      }
   }

   /**
    * Build a "polyline" element.
    *
    * @param xmlNode the node
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildPolyline(XMLNode xmlNode, Viewport viewport) {
      Polyline polyline = new Polyline();
      String pointsAttribute = xmlNode.getAttributeValue("points");

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");
      while (tokenizer.hasMoreTokens()) {
         String points = tokenizer.nextToken();
         StringTokenizer tokenizer2 = new StringTokenizer(points, ",");
         double x = ParserUtils.parseDoubleProtected(tokenizer2.nextToken(), true, viewport);
         double y = ParserUtils.parseDoubleProtected(tokenizer2.nextToken(), false, viewport);
         polyline.getPoints().add(x);
         polyline.getPoints().add(y);
      }

      return polyline;
   }

   public static void buildFEGaussianBlur(FilterSpec spec, XMLNode node) {
      double stdDeviation = 0d;

      String resultId = node.getAttributeValue(RESULT);
      if (node.hasAttribute(STD_DEVIATION)) {
         String stdDevS = node.getAttributeValue(STD_DEVIATION);
         stdDevS = ParserUtils.parseFirstArgument(stdDevS);
         stdDeviation = ParserUtils.parseDoubleProtected(stdDevS);
      }
      FilterSpec.FEGaussianBlur effect = new FilterSpec.FEGaussianBlur(resultId, stdDeviation);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEDropShadow(FilterSpec spec, XMLNode node, Viewport viewport) {
      double dx = node.getAttributeValueAsDouble(DX, true, viewport);
      double dy = node.getAttributeValueAsDouble(DY, true, viewport);
      double opacity = 1d;
      double stdDeviation = 0d;
      Color col = Color.BLACK;

      String resultId = node.getAttributeValue(RESULT);
      if (node.hasAttribute(FLOOD_OPACITY)) {
         opacity = node.getAttributeValueAsDouble(FLOOD_OPACITY, 1d);
      }
      if (node.hasAttribute(STD_DEVIATION)) {
         String stdDevS = node.getAttributeValue(STD_DEVIATION);
         stdDevS = ParserUtils.parseFirstArgument(stdDevS);
         stdDeviation = ParserUtils.parseDoubleProtected(stdDevS);
      }
      if (node.hasAttribute(FLOOD_COLOR)) {
         String colorS = node.getAttributeValue(FLOOD_COLOR);
         col = ParserUtils.getColor(colorS, opacity);
      }
      FilterSpec.FEDropShadow effect = new FilterSpec.FEDropShadow(resultId, dx, dy, stdDeviation, col);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEFlood(FilterSpec spec, XMLNode node, Viewport viewport) {
      double x = node.getAttributeValueAsDouble(X, true, viewport);
      double y = node.getAttributeValueAsDouble(Y, true, viewport);
      double width = node.getAttributeValueAsDouble(WIDTH, true, viewport);
      double height = node.getAttributeValueAsDouble(HEIGHT, true, viewport);
      double opacity = 1d;
      Color col = Color.BLACK;
      String resultId = node.getAttributeValue(RESULT);

      if (node.hasAttribute(FLOOD_OPACITY)) {
         opacity = node.getAttributeValueAsDouble(FLOOD_OPACITY, 1d);
      }
      if (node.hasAttribute(FLOOD_COLOR)) {
         String colorS = node.getAttributeValue(FLOOD_COLOR);
         col = ParserUtils.getColor(colorS, opacity);
      }
      FilterSpec.FEFlood effect = new FilterSpec.FEFlood(resultId, x, y, width, height, col);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEOffset(FilterSpec spec, XMLNode node, Viewport viewport) {
      double dx = node.getAttributeValueAsDouble(DX, true, viewport);
      double dy = node.getAttributeValueAsDouble(DY, true, viewport);
      String resultId = node.getAttributeValue(RESULT);

      FilterSpec.FEOffset effect = new FilterSpec.FEOffset(resultId, dx, dy);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEImage(FilterSpec spec, URL url, XMLNode node, Viewport viewport) {
      double x = node.getAttributeValueAsDouble(X, true, viewport);
      double y = node.getAttributeValueAsDouble(Y, true, viewport);
      double width = node.getAttributeValueAsDouble(WIDTH, true, viewport);
      double height = node.getAttributeValueAsDouble(HEIGHT, true, viewport);
      String hrefAttribute = node.getAttributeValue(XLINK_HREF);
      String resultId = node.getAttributeValue(RESULT);

      Image image = null;
      URL imageUrl = null;
      try {
         imageUrl = new URL(hrefAttribute);
      } catch (MalformedURLException ex) {
         try {
            imageUrl = new URL(url, hrefAttribute);
         } catch (MalformedURLException ex1) {
         }
      }
      if (imageUrl != null) {
         image = new Image(imageUrl.toString(), width, height, true, true);
      }
      FilterSpec.FEImage effect = new FilterSpec.FEImage(resultId, x, y, image);
      spec.addEffect(resultId, effect);
   }

   public static void buildFESpecularLighting(FilterSpec spec, XMLNode node, Viewport viewport) {
      XMLNode child = node.getFirstChild();
      if (child != null) {
         switch (child.getName()) {
            case FE_DISTANT_LIGHT: {
               double surfaceScale = node.getAttributeValueAsDouble(SURFACE_SCALE, 1.5d);
               double specularConstant = node.getAttributeValueAsDouble(SPECULAR_CONSTANT, 0.3d);
               double specularExponent = node.getAttributeValueAsDouble(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double azimuth = child.getAttributeValueAsDouble(AZIMUTH);
               double elevation = child.getAttributeValueAsDouble(ELEVATION);
               Light.Distant light = new Light.Distant(azimuth, elevation, col);
               String resultId = node.getAttributeValue(RESULT);
               FESpecularLighting effect = new FESpecularLighting(resultId, specularConstant, specularExponent, surfaceScale, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
         }
      }
   }

   public static void buildFEMerge(FilterSpec spec, XMLNode node) {
      String resultId = node.getAttributeValue(RESULT);
      FilterSpec.FEMerge effect = new FilterSpec.FEMerge(resultId);
      spec.addEffect(resultId, effect);

      Iterator<XMLNode> it = node.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode child = it.next();
         if (child.getName().equals(FE_MERGE_NODE)) {
            String in = child.getAttributeValue(IN);
            if (in != null) {
               effect.addMergeNode(in);
            }
         }
      }
   }
}
