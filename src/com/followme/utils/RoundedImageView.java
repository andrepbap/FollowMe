package com.followme.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.*;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

	public RoundedImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		Bitmap b = ((BitmapDrawable) drawable).getBitmap();
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

		int w = getWidth(), h = getHeight();

		Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
		canvas.drawBitmap(roundBitmap, 0, 0, null);

	}

	public static Bitmap getCroppedBitmap(Bitmap bmp1, int radius) {

		Bitmap sbmp;
		if (bmp1.getWidth() != radius || bmp1.getHeight() != radius){
			sbmp = Bitmap.createScaledBitmap(bmp1, radius, radius, false);
		}
		else{
			sbmp = bmp1;
		}
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffa19774;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,
				sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);
		

		return output;
	}
	
	public static Bitmap getMergedBitmap(Bitmap bmp1, Bitmap bmp2, Context ctx) {
		Bitmap bitmap = null;

		try {
			//pega o tamanho da tela
			WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			//int height = size.y;
			
			double proporcaoBmp1 = 0.2; //% da largura da tela
			double proporcaoBmp2 = 0.9; //% da largura do bmp1
			double proporcaoMargemBmp1 = 0.1; //% da largura do bmp1
			
			//tamanhos originais do bmp1
			int sizeBmp1X = 617;
			int sizeBmp1Y = 766;
			
			//redimensiona bmp1
			int newSizeBmp1X = (int) (width * proporcaoBmp1); 
			int newSizeBmp1Y = (newSizeBmp1X * sizeBmp1Y) / sizeBmp1X; // mantem proporção
			
			//redimensiona bmp2
			int newSizeBmp2X = (int) (newSizeBmp1X * proporcaoBmp2);
			int newSizeBmp2Y = newSizeBmp2X;
			int margemBmp2 = (int) (newSizeBmp1X * proporcaoMargemBmp1);
			
			bitmap = Bitmap.createBitmap(newSizeBmp1X, newSizeBmp1Y, Config.ARGB_8888);
			Canvas c = new Canvas(bitmap);

			Drawable drawable1 = new BitmapDrawable(bmp1);
			Drawable drawable2 = new BitmapDrawable(bmp2);

			drawable1.setBounds(0, 0, newSizeBmp1X, newSizeBmp1Y);
			drawable2.setBounds(margemBmp2, margemBmp2, newSizeBmp2X, newSizeBmp2Y);
			drawable1.draw(c);
			drawable2.draw(c);

		} catch (Exception e) {
		}

		return bitmap;
	}

}
