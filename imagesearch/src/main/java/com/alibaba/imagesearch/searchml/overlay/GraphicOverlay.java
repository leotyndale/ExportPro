/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may context a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.imagesearch.searchml.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * A view which renders a series of custom graphics to be overlayed on top of an associated preview
 * (i.e., the camera preview). The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.
 *
 * <p>Supports scaling and mirroring of the graphics relative the camera's preview properties. The
 * idea is that detection items are expressed in terms of an image size, but need to be scaled up
 * to the full view size, and also mirrored in the case of the front-facing camera.
 *
 * <p>Associated {@link Graphic} items should use the following methods to convert to view
 * coordinates for the graphics that are drawn:
 *
 * <ol>
 *   <li>{@link Graphic#scale(float)} adjusts the size of the supplied value from the image scale
 *       to the view scale.
 *   <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
 *       coordinate from the image's coordinate system to the view coordinate system.
 * </ol>
 */
public class GraphicOverlay extends View {
  private final Object mLock = new Object();
  public final List<Graphic> mGraphics = new ArrayList<>();
  // Matrix for transforming from image coordinates to overlay view coordinates.
  private final Matrix mTransformationMatrix = new Matrix();

  private int mImageWidth;
  private int mImageHeight;
  // The factor of overlay View size to image size. Anything in the image coordinates need to be
  // scaled by this amount to fit with the area of overlay View.
  private float mScaleFactor = 1.0f;
  // The number of horizontal pixels needed to be cropped on each side to fit the image with the
  // area of overlay View after scaling.
  private float mPostScaleWidthOffset;
  // The number of vertical pixels needed to be cropped on each side to fit the image with the
  // area of overlay View after scaling.
  private float mPostScaleHeightOffset;
  private boolean mIsImageFlipped;
  private boolean mNeedUpdateTransformation = true;

  /**
   * Base class for a custom graphics object to be rendered within the graphic overlay. Subclass
   * this and implement the {@link Graphic#draw(Canvas)} method to define the graphics element. Add
   * instances to the overlay using {@link GraphicOverlay#add(Graphic)}.
   */
  public abstract static class Graphic {
    private final GraphicOverlay mOverlay;

    public Graphic(GraphicOverlay overlay) {
      this.mOverlay = overlay;
    }

    /**
     * Draw the graphic on the supplied canvas. Drawing should use the following methods to convert
     * to view coordinates for the graphics that are drawn:
     *
     * <ol>
     *   <li>{@link Graphic#scale(float)} adjusts the size of the supplied value from the image
     *       scale to the view scale.
     *   <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
     *       coordinate from the image's coordinate system to the view coordinate system.
     * </ol>
     *
     * @param canvas drawing canvas
     */
    public abstract void draw(Canvas canvas);

    /** Adjusts the supplied value from the image scale to the view scale. */
    public float scale(float imagePixel) {
      return imagePixel * mOverlay.mScaleFactor;
    }

    /**
     * Adjusts the x coordinate from the image's coordinate system to the view coordinate system.
     */
    public float translateX(float x) {
      if (mOverlay.mIsImageFlipped) {
        return mOverlay.getWidth() - (scale(x) - mOverlay.mPostScaleWidthOffset);
      } else {
        return scale(x) - mOverlay.mPostScaleWidthOffset;
      }
    }

    /**
     * Adjusts the y coordinate from the image's coordinate system to the view coordinate system.
     */
    public float translateY(float y) {
      return scale(y) - mOverlay.mPostScaleHeightOffset;
    }

    /**
     * Returns a {@link Matrix} for transforming from image coordinates to overlay view coordinates.
     */
    public Matrix getTransformationMatrix() {
      return mOverlay.mTransformationMatrix;
    }

  }

  public GraphicOverlay(Context context, AttributeSet attrs) {
    super(context, attrs);
    addOnLayoutChangeListener(
        (view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
            mNeedUpdateTransformation = true);
  }

  /** Removes all graphics from the overlay. */
  public void clear() {
    synchronized (mLock) {
      mGraphics.clear();
    }
//    postInvalidate();
  }

  /** Adds a graphic to the overlay. */
  public void add(Graphic graphic) {
    synchronized (mLock) {
      mGraphics.add(graphic);
    }
  }

  /**
   * Sets the source information of the image being processed by detectors, including size and
   * whether it is flipped, which informs how to transform image coordinates later.
   *
   * @param imageWidth the width of the image sent to ML Kit detectors
   * @param imageHeight the height of the image sent to ML Kit detectors
   * @param isFlipped whether the image is flipped. Should set it to true when the image is from the
   *     front camera.
   */
  public void setImageSourceInfo(int imageWidth, int imageHeight, boolean isFlipped) {
//    Preconditions.checkState(imageWidth > 0, "image width must be positive");
//    Preconditions.checkState(imageHeight > 0, "image height must be positive");
    synchronized (mLock) {
      this.mImageWidth = imageWidth;
      this.mImageHeight = imageHeight;
      this.mIsImageFlipped = isFlipped;
      mNeedUpdateTransformation = true;
    }
    postInvalidate();
  }

  private void updateTransformationIfNeeded() {
    if (!mNeedUpdateTransformation || mImageWidth <= 0 || mImageHeight <= 0) {
      return;
    }
    float viewAspectRatio = (float) getWidth() / getHeight();
    float imageAspectRatio = (float) mImageWidth / mImageHeight;
    mPostScaleWidthOffset = 0;
    mPostScaleHeightOffset = 0;
    if (viewAspectRatio > imageAspectRatio) {
      // The image needs to be vertically cropped to be displayed in this view.
      mScaleFactor = (float) getWidth() / mImageWidth;
      mPostScaleHeightOffset = ((float) getWidth() / imageAspectRatio - getHeight()) / 2;
    } else {
      // The image needs to be horizontally cropped to be displayed in this view.
      mScaleFactor = (float) getHeight() / mImageHeight;
      mPostScaleWidthOffset = ((float) getHeight() * imageAspectRatio - getWidth()) / 2;
    }

    mTransformationMatrix.reset();
    mTransformationMatrix.setScale(mScaleFactor, mScaleFactor);
    mTransformationMatrix.postTranslate(-mPostScaleWidthOffset, -mPostScaleHeightOffset);

    if (mIsImageFlipped) {
      mTransformationMatrix.postScale(-1f, 1f, getWidth() / 2f, getHeight() / 2f);
    }

    mNeedUpdateTransformation = false;
  }

  /** Draws the overlay with its associated graphic objects. */
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    synchronized (mLock) {
      updateTransformationIfNeeded();

      for (Graphic graphic : mGraphics) {
        graphic.draw(canvas);
      }
    }
  }
}
