package com.example.scandroid;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Generates a profile picture for when users remove their picture or do not have one yet
 */
//OpenAI, 2024, ChatGPT, Create a bitmap of letter using canvas and paint
public class ProfilePictureGenerator {
    /**
     * Generate the bitmap of the profile picture based on the user's name
     * @param name The user's name
     * @return
     * A Bitmap profile picture
     */
    public Bitmap generatePictureBitmap(String name) {
        // Create a new Bitmap with the specified width and height
        int width = 200;
        int height = 200;
        name = name.toUpperCase();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Create a Canvas to draw on the Bitmap
        Canvas canvas = new Canvas(bitmap);

        char firstLetter = name.charAt(0);
        int backgroundColor = generateColor(firstLetter);

        canvas.drawColor(backgroundColor);

        // Create a Paint object for drawing text
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(180);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        // Generate a random letter

        // Calculate the position to center the letter
        float x = (width - paint.measureText(String.valueOf(firstLetter))) / 2;
        float y = (height - paint.ascent() - paint.descent()) / 2;

        // Draw the random letter on the canvas
        canvas.drawText(String.valueOf(name.toUpperCase().charAt(0)), x, y, paint);

        return bitmap;
    }

    /**
     * Deterministically generate a color for the letter and background of profile picture
     * @param firstLetter First letter of user's name used to determine colors
     * @return
     * A Color
     */
    private static int generateColor(char firstLetter) {
        if ('A' <=firstLetter && firstLetter <= 'I'){
            return Color.rgb(145,108,219);
        }
        else if ('I' <firstLetter && firstLetter <= 'Q'){
            return Color.rgb(108,219,182);
        }
        else if ('Q' < firstLetter){
            return Color.rgb(219,182,108);
        }
        else {
            return Color.rgb(145,108,219);
        }
    }
}

