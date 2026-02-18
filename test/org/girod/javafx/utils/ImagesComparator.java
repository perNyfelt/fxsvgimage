/*------------------------------------------------------------------------------
 * Copyright (C) 2026 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.girod.javafx.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * This class allows to compare two images.
 *
 * @since 1.5
 */
public class ImagesComparator {
   private final Params params;

   public ImagesComparator() {
      this.params = new Params();
   }

   public ImagesComparator(Params params) {
      this.params = params;
   }

   /**
    * Return the comparison parameters.
    *
    * @return the parameters
    */
   public Params getParams() {
      return params;
   }

   /**
    * Compare two images.
    *
    * @param fileA the first image
    * @param fileB the second image
    * @return the comparison result
    */
   public Result compareImages(File fileA, File fileB) {
      try {
         BufferedImage biA = ImageIO.read(fileA);
         BufferedImage biB = ImageIO.read(fileB);
         return compareImages(biA, biB);
      } catch (IOException e) {
         return new Result(Result.INVALID);
      }
   }

   /**
    * Compare two images.
    *
    * @param fileA the first image
    * @param fileB the second image
    * @return the comparison result
    */
   public Result compareImages(URL fileA, URL fileB) {
      try {
         BufferedImage biA = ImageIO.read(fileA);
         BufferedImage biB = ImageIO.read(fileB);
         return compareImages(biA, biB);
      } catch (IOException e) {
         return new Result(Result.INVALID);
      }
   }

   /**
    * Compare two images.
    *
    * @param biA the first image
    * @param fileB the second image
    * @return the comparison result
    */
   public Result compareImages(BufferedImage biA, URL fileB) {
      try {
         BufferedImage biB = ImageIO.read(fileB);
         return compareImages(biA, biB);
      } catch (IOException e) {
         return new Result(Result.INVALID);
      }
   }

   /**
    * Compare two images.
    *
    * @param biA the first image
    * @param fileB the second image
    * @return the comparison result
    */
   public Result compareImages(BufferedImage biA, File fileB) {
      try {
         BufferedImage biB = ImageIO.read(fileB);
         return compareImages(biA, biB);
      } catch (IOException e) {
         return new Result(Result.INVALID);
      }
   }

   /**
    * Compare two images.
    *
    * @param biA the first image
    * @param biB the second image
    * @return the comparison result
    */
   public Result compareImages(BufferedImage biA, BufferedImage biB) {
      int countDiffPixels = 0;
      int widthA = biA.getWidth(null);
      int heightA = biA.getHeight(null);
      int widthB = biB.getWidth(null);
      int heightB = biB.getHeight(null);
      int width = widthA < widthB ? widthA : widthB;
      int height = heightA < heightB ? heightA : heightB;
      for (int i = 0; i < width; i++) {
         for (int j = 0; j < height; j++) {
            int rgbA = biA.getRGB(i, j);
            int rgbB = biB.getRGB(i, j);
            int deltaRGB = rgbB - rgbA;
            if (deltaRGB < 0) {
               deltaRGB = -deltaRGB;
            }
            if (deltaRGB > params.deltaRGB) {
               countDiffPixels++;
            }
         }
      }
      if (!params.crop) {
         int deltaWidth = widthB - widthA;
         if (deltaWidth < 0) {
            deltaWidth = -deltaWidth;
         }
         int deltaHeight = heightB - heightA;
         if (deltaHeight < 0) {
            deltaHeight = -deltaHeight;
         }
         if (deltaWidth == 0 && deltaHeight != 0) {
            countDiffPixels += deltaHeight;
         } else if (deltaWidth != 0 && deltaHeight == 0) {
            countDiffPixels += deltaWidth;
         } else if (deltaWidth != 0 && deltaHeight != 0) {
            countDiffPixels += deltaWidth * deltaHeight;
         }
      }
      boolean isNotEquals;
      float percentDiffPixels = (float) countDiffPixels / (float) (width * height) * 100f;
      if (params.deltaInPercent) {
         isNotEquals = percentDiffPixels > params.deltaPixels;
      } else {
         isNotEquals = countDiffPixels > params.deltaPixels;
      }
      short state = isNotEquals ? Result.NO_EQUALS : Result.EQUALS;
      return new Result(state, countDiffPixels, percentDiffPixels);
   }

   /**
    * The comparison parameters.
    *
    * @since 1.3.21
    */
   public static class Params {
      /**
       * True if the images are cropped to compare the same size.
       */
      public boolean crop = false;
      /**
       * True the maximum difference in rgb value for which two pixels will be considered equal.
       */      
      public int deltaRGB = 0;
      /**
       * True the maximum difference in tne number of different pixels or percentage for the images to be considered equal.
       */           
      public float deltaPixels = 0;
      /**
       * True if the check for equality is in percentage of different pixels.
       */
      public boolean deltaInPercent = false;

      public Params() {
      }
   }

   /**
    * The comparison result.
    *
    * @since 1.3.21
    */
   public static class Result {
      /**
       * The state for an invalid image.
       */
      public static final short INVALID = -1;
      /**
       * The state for images which are considered equal.
       */
      public static final short EQUALS = 0;
      /**
       * The state for images which are considered not equal.
       */
      public static final short NO_EQUALS = 1;
      private final short state;
      private int diffPixels = 0;
      private float percentDiffPixels = 0;

      private Result(short state) {
         this.state = state;
      }

      private Result(short state, int diffPixels, float percentDiffPixels) {
         this.state = state;
         this.diffPixels = diffPixels;
         this.percentDiffPixels = percentDiffPixels;
      }

      /**
       * Return the number of pixels which are different between the images.
       *
       * @return the number of pixels
       */
      public int countDiffPixels() {
         return diffPixels;
      }

      /**
       * Return the percentage of pixels which are different between the images.
       *
       * @return the number of pixels
       */
      public float gePercentDiffPixels() {
         return percentDiffPixels;
      }

      /**
       * Return true if the images are considered as equal.
       *
       * @return true if the images are considered as equal
       */
      public boolean isEquals() {
         return state == EQUALS;
      }
      
      /**
       * Return the comparison state.
       *
       * @return the comparison state
       */
      public short getState() {
         return state;
      }      
   }
}
