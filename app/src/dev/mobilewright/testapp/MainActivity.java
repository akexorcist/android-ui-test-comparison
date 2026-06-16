package dev.mobilewright.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Deterministic UI covering the basic UI interactions for the tool benchmark:
 *   tap, text entry, long press, swipe/scroll — each with a verifiable result.
 * Wrapped in a ScrollView so swipe/scroll reveals an off-screen bottom marker.
 */
public class MainActivity extends Activity {

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 64, 32, 32);

        // ── Tap → counter ─────────────────────────────────────────
        final TextView counter = new TextView(this);
        counter.setText("Count: 0");
        counter.setContentDescription("counter");
        counter.setTextSize(24);
        root.addView(counter);

        Button tapBtn = new Button(this);
        tapBtn.setText("TAP ME");
        tapBtn.setContentDescription("tap-button");
        tapBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                count++;
                counter.setText("Count: " + count);
            }
        });
        root.addView(tapBtn);

        // ── Text entry → echo ─────────────────────────────────────
        final EditText input = new EditText(this);
        input.setHint("type here");
        input.setContentDescription("input-field");
        root.addView(input);

        final TextView echo = new TextView(this);
        echo.setText("Echo: ");
        echo.setContentDescription("echo");
        root.addView(echo);

        Button echoBtn = new Button(this);
        echoBtn.setText("ECHO");
        echoBtn.setContentDescription("echo-button");
        echoBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                echo.setText("Echo: " + input.getText().toString());
            }
        });
        root.addView(echoBtn);

        // ── Long press → status ───────────────────────────────────
        final TextView longStatus = new TextView(this);
        longStatus.setText("Long: idle");
        longStatus.setContentDescription("long-status");
        root.addView(longStatus);

        Button longBtn = new Button(this);
        longBtn.setText("LONG PRESS HERE");
        longBtn.setContentDescription("long-button");
        longBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                longStatus.setText("Long: pressed");
                return true;
            }
        });
        root.addView(longBtn);

        // ── Tall spacer pushes the bottom marker off-screen ───────
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1600));
        root.addView(spacer);

        // ── Swipe/scroll target (off-screen until scrolled) ───────
        final TextView bottomMarker = new TextView(this);
        bottomMarker.setText("BOTTOM REACHED");
        bottomMarker.setContentDescription("bottom-marker");
        bottomMarker.setTextSize(24);
        root.addView(bottomMarker);

        ScrollView scroll = new ScrollView(this);
        scroll.setContentDescription("scroll");
        scroll.addView(root);
        setContentView(scroll);
    }
}
