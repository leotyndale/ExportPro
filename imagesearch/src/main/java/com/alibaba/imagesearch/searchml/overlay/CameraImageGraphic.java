package com.alibaba.imagesearch.searchml.overlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/** Draw camera image to background. */
public class CameraImageGraphic extends GraphicOverlay.Graphic {

  private final Bitmap mBitmap;

  public CameraImageGraphic(GraphicOverlay overlay, Bitmap bitmap) {
    super(overlay);
    this.mBitmap = bitmap;
  }

  @Override
  public void draw(Canvas canvas) {
    canvas.drawBitmap(mBitmap, getTransformationMatrix(), null);
  }
}
