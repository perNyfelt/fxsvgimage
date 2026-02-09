/*
Copyright (c) 2025, Hervé Girod
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
package org.girod.javafx.svgimage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;
import org.girod.javafx.svgimage.xml.parsers.SVGPathParser;

/**
 * Test that SVG paths with implicit repeated commands are parsed correctly.
 * In SVG path data, extra coordinate pairs after a command are treated as
 * implicit repeated commands (e.g., extra pairs after M become implicit L).
 *
 * @since 1.4
 */
public class SVGLoaderImplicitCommandsTest {

   public SVGLoaderImplicitCommandsTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test that extra coordinate pairs after M are split into an implicit L command.
    * Uses SVGPathParser directly with a non-scaling viewport for predictable output.
    */
   @Test
   public void testImplicitLineToAfterMoveToContent() {
      System.out.println("SVGLoaderImplicitCommandsTest : testImplicitLineToAfterMoveToContent");
      SVGPathParser parser = new SVGPathParser();
      Viewport viewport = new Viewport();

      parser.parse("M10 20 30 40", viewport);
      assertEquals("M 10.0 20.0 L 30.0 40.0", parser.getContent());
   }

   /**
    * Test that extra coordinate pairs after m are split into an implicit l command.
    */
   @Test
   public void testImplicitRelativeLineToAfterMoveTo() {
      System.out.println("SVGLoaderImplicitCommandsTest : testImplicitRelativeLineToAfterMoveTo");
      SVGPathParser parser = new SVGPathParser();
      Viewport viewport = new Viewport();

      parser.parse("m10 20 30 40", viewport);
      assertEquals("m 10.0 20.0 l 30.0 40.0", parser.getContent());
   }

   /**
    * Test that extra coordinate pairs after l are split into repeated l commands.
    */
   @Test
   public void testImplicitRepeatedLineTo() {
      System.out.println("SVGLoaderImplicitCommandsTest : testImplicitRepeatedLineTo");
      SVGPathParser parser = new SVGPathParser();
      Viewport viewport = new Viewport();

      parser.parse("M0 0 l10 20 30 40", viewport);
      assertEquals("M 0.0 0.0 l 10.0 20.0 l 30.0 40.0", parser.getContent());
   }

   /**
    * Test loading an SVG with implicit LineTo commands after MoveTo.
    * The path "M784-120 532-372..." has extra coordinate pairs after M
    * that should be treated as implicit LineTo commands.
    * Also tests negative viewBox coordinates (viewBox="0 -960 960 960").
    */
   @Test
   public void testImplicitLineToAfterMoveTo() throws Exception {
      System.out.println("SVGLoaderImplicitCommandsTest : testImplicitLineToAfterMoveTo");
      URL url = this.getClass().getResource("searchIcon.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be an SVGPath", child instanceof SVGPath);

      String content = ((SVGPath) child).getContent();
      assertNotNull("SVGPath content should not be null", content);
      assertTrue("Content should contain implicit L command from M's extra pair",
              content.contains("L "));
      // The original path has "l252 252-56 56" which should split into two l commands
      int firstL = content.indexOf("l ");
      assertTrue("Content should contain a relative l command", firstL >= 0);
      int secondL = content.indexOf("l ", firstL + 1);
      assertTrue("Content should contain a second l command from implicit repeat", secondL > firstL);
   }

   /**
    * Test loading the same SVG from a string.
    */
   @Test
   public void testImplicitLineToFromString() throws Exception {
      System.out.println("SVGLoaderImplicitCommandsTest : testImplicitLineToFromString");
      String svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"24px\" viewBox=\"0 -960 960 960\" width=\"24px\" fill=\"#1f1f1f\">"
              + "<path d=\"M784-120 532-372q-30 24-69 38t-83 14q-109 0-184.5-75.5T120-580q0-109 75.5-184.5T380-840q109 0 184.5 75.5T640-580q0 44-14 83t-38 69l252 252-56 56ZM380-400q75 0 127.5-52.5T560-580q0-75-52.5-127.5T380-760q-75 0-127.5 52.5T200-580q0 75 52.5 127.5T380-400Z\"/>"
              + "</svg>";

      SVGImage result = SVGLoader.load(svg);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be an SVGPath", child instanceof SVGPath);

      String content = ((SVGPath) child).getContent();
      assertNotNull("SVGPath content should not be null", content);
      assertTrue("Content should contain implicit L command from M's extra pair",
              content.contains("L "));
   }
}
