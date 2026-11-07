package com.kplay.iptv;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "kplay_settings";
    private static final String KEY_PLAYLIST_URL = "playlist_url";

    private SharedPreferences preferences;
    private TextView playlistStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        playlistStatus = findViewById(R.id.txtPlaylistStatus);
        refreshPlaylistStatus();

        findViewById(R.id.btnLive).setOnClickListener(v -> showCatalogMessage(getString(R.string.live)));
        findViewById(R.id.btnMovies).setOnClickListener(v -> showCatalogMessage(getString(R.string.movies)));
        findViewById(R.id.btnSeries).setOnClickListener(v -> showCatalogMessage(getString(R.string.series)));
        findViewById(R.id.btnAccount).setOnClickListener(v -> showAccount());
        findViewById(R.id.btnChangePlaylist).setOnClickListener(v -> showPlaylistDialog());
        findViewById(R.id.btnSettings).setOnClickListener(v -> showSettings());
        findViewById(R.id.btnReload).setOnClickListener(v -> reloadStatus());
        findViewById(R.id.btnFavorites).setOnClickListener(v -> showFavorites());
        findViewById(R.id.btnSearch).setOnClickListener(v -> showSearchInfo());
        findViewById(R.id.btnExit).setOnClickListener(v -> finish());
    }

    private void showCatalogMessage(String category) {
        String message = preferences.getString(KEY_PLAYLIST_URL, "").isEmpty()
                ? getString(R.string.catalog_needs_playlist)
                : getString(R.string.catalog_playback_coming);
        new AlertDialog.Builder(this)
                .setTitle(category)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showPlaylistDialog() {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        input.setHint(R.string.playlist_url_hint);
        input.setText(preferences.getString(KEY_PLAYLIST_URL, ""));
        input.setSelection(input.length());
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        new AlertDialog.Builder(this)
                .setTitle(R.string.change_playlist)
                .setMessage(R.string.playlist_dialog_message)
                .setView(input)
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(R.string.remove_playlist, (dialog, which) -> {
                    preferences.edit().remove(KEY_PLAYLIST_URL).apply();
                    refreshPlaylistStatus();
                    Toast.makeText(this, R.string.playlist_removed, Toast.LENGTH_SHORT).show();
                })
                .setPositiveButton(R.string.save, (dialog, which) -> savePlaylist(input.getText().toString()))
                .show();
    }

    private void savePlaylist(String value) {
        String url = value.trim();
        if (!(url.startsWith("https://") || url.startsWith("http://")) || url.length() <= 8) {
            Toast.makeText(this, R.string.invalid_playlist_url, Toast.LENGTH_LONG).show();
            return;
        }
        preferences.edit().putString(KEY_PLAYLIST_URL, url).apply();
        refreshPlaylistStatus();
        Toast.makeText(this, R.string.playlist_saved, Toast.LENGTH_SHORT).show();
    }

    private void showAccount() {
        boolean configured = !preferences.getString(KEY_PLAYLIST_URL, "").isEmpty();
        new AlertDialog.Builder(this)
                .setTitle(R.string.account)
                .setMessage(configured ? R.string.account_playlist_configured : R.string.account_playlist_missing)
                .setPositiveButton(R.string.change_playlist, (dialog, which) -> showPlaylistDialog())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showSettings() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.settings)
                .setMessage(R.string.settings_info)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void reloadStatus() {
        refreshPlaylistStatus();
        Toast.makeText(this, R.string.status_refreshed, Toast.LENGTH_SHORT).show();
    }

    private void refreshPlaylistStatus() {
        if (playlistStatus == null) return;
        boolean configured = !preferences.getString(KEY_PLAYLIST_URL, "").isEmpty();
        playlistStatus.setText(configured ? R.string.playlist_configured : R.string.playlist_not_configured);
        playlistStatus.setCompoundDrawablesWithIntrinsicBounds(
                configured ? R.drawable.status_dot_online : R.drawable.status_dot_offline, 0, 0, 0);
    }

    private void showFavorites() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.favorites)
                .setMessage(R.string.favorites_empty)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showSearchInfo() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.search)
                .setMessage(R.string.search_info)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
