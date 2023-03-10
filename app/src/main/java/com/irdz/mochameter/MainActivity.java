package com.irdz.mochameter;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.irdz.mochameter.dao.impl.UserDaoImpl;
import com.irdz.mochameter.databinding.ActivityMainBinding;
import com.irdz.mochameter.service.BannedService;
import com.irdz.mochameter.ui.scan.ScanActivity;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private boolean bannedUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        exitForBannedUser();

        if(!bannedUser) {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            BottomNavigationView navView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_ranking, R.id.navigation_evaluation)
//            , R.id.navigation_profile) TODO
                .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupWithNavController(binding.navView, navController);

            FloatingActionButton btn_scan = findViewById(R.id.btn_scan);
            btn_scan.setOnClickListener(v -> {
                Intent intent = new Intent(this, ScanActivity.class);
                startActivity(intent);
                ScanActivity.coffeeRead = false;
            });
        }
    }

    private void exitForBannedUser() {
        //TODO your background code
        while(BannedService.getInstance() == null) {
            Log.d(TAG, "waiting for database connection");
        }
        bannedUser = BannedService.getInstance().isBannedUser(
            UserDaoImpl.getAndroidId(this),
            UserDaoImpl.getLoggedInUserId(this)
        );
        if(bannedUser) {
            runOnUiThread(() -> showPopupBanned());
        }
    }

    private void showPopupBanned() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_banned, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("User Banned");

        builder.setNegativeButton("OK", (dialog, which) -> exit(dialog));

        AlertDialog dialog = builder.create();

        dialog.setOnDismissListener(this::exit);

        dialog.show();
    }

    private void exit(final DialogInterface dialog) {
        dialog.dismiss();
        finish();
        System.exit(0);
    }

}