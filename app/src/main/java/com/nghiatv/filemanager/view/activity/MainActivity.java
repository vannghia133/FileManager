package com.nghiatv.filemanager.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nghiatv.filemanager.R;
import com.nghiatv.filemanager.util.FileUtil;
import com.nghiatv.filemanager.util.PreferenceUtil;
import com.nghiatv.filemanager.view.adapter.FileAdapter;
import com.nghiatv.filemanager.view.adapter.OnItemSelectedListener;
import com.nghiatv.filemanager.view.dialog.InputDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 100;

    private static final String SAVED_DIRECTORY = "SAVED_DIRECTORY";
    private static final String SAVED_SELECTION = "SAVED_SELECTION";
    private static final String EXTRA_NAME = "EXTRA_NAME";
    private static final String EXTRA_TYPE = "EXTRA_TYPE";

    private static final String AUDIO = "audio";
    private static final String IMAGE = "image";
    private static final String VIDEO = "video";
    private static final String DCIM = "DCIM";
    private static final String DOWNLOAD = "Download";
    private static final String MOVIES = "Movies";
    private static final String MUSIC = "Music";
    private static final String PICTURES = "Pictures";

    //----------------------------------------------------------------------------------------------

    private CollapsingToolbarLayout toolbarLayout;
    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fabCreate;
    private TextView txtHeader;
    private RecyclerView rcvFile;

    private FileAdapter fileAdapter;
    private File currentDirectory;
    private String name;
    private String type;

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initActivityFromIntent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!requestPermissions()) {
        }

        initializeComponents();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
            return;
        }

        if (fileAdapter.anySelected()) {
            fileAdapter.clearSelection();
            return;
        }

        if (!FileUtil.isStorage(currentDirectory)) {
            setPath(currentDirectory.getParentFile());
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                loadIntoRecyclerview();
            } else {
                Snackbar.make(coordinatorLayout, "Permission required", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                goToApplicationSettings();
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {

        if (fileAdapter != null) {
            fileAdapter.refresh();
        }

        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        fileAdapter.select(savedInstanceState.getIntegerArrayList(SAVED_SELECTION));

        String path = savedInstanceState.getString(SAVED_DIRECTORY, FileUtil.getInternalStorage().getPath());
        if (currentDirectory != null) {
            setPath(new File(path));
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putIntegerArrayList(SAVED_SELECTION, fileAdapter.getSelectedPositions());
        outState.putString(SAVED_DIRECTORY, FileUtil.getPath(currentDirectory));

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionDelete:
                actionDelete();
                return true;

            case R.id.actionRename:
                actionRename();
                return true;

            case R.id.actionSearch:
                actionSearch();
                return true;

            case R.id.actionCopy:
                actionCopy();
                return true;

            case R.id.actionMove:
                actionMove();
                return true;

            case R.id.actionSend:
                actionSend();
                return true;

            case R.id.actionSort:
                actionSort();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (fileAdapter != null) {
            int count = fileAdapter.getSelectedItemCount();
            menu.findItem(R.id.actionDelete).setVisible(count >= 1);
            menu.findItem(R.id.actionRename).setVisible(count >= 1);
            menu.findItem(R.id.actionSearch).setVisible(count == 0);
            menu.findItem(R.id.actionCopy).setVisible(count >= 1 && name == null && type == null);
            menu.findItem(R.id.actionMove).setVisible(count >= 1 && name == null && type == null);
            menu.findItem(R.id.actionSend).setVisible(count >= 1);
            menu.findItem(R.id.actionSort).setVisible(count == 0);

        }

        return super.onPrepareOptionsMenu(menu);
    }

    //----------------------------------------------------------------------------------------------

    private void initActivityFromIntent() {
        name = getIntent().getStringExtra(EXTRA_NAME);
        type = getIntent().getStringExtra(EXTRA_TYPE);

        if (type != null) {
            switch (type) {
                case AUDIO:
                    setTheme(R.style.app_theme_Audio);
                    break;

                case IMAGE:
                    setTheme(R.style.app_theme_Image);
                    break;

                case VIDEO:
                    setTheme(R.style.app_theme_Video);
                    break;

                default:
                    break;
            }
        }
    }

    private boolean requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        } else {
            if (isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return false;
            }

            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , REQUEST_CODE_PERMISSION);

            return true;
        }
    }

    private boolean isGranted(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void loadIntoRecyclerview() {
        final Context context = this;

        if (name != null) {
            fileAdapter.addAll(FileUtil.searchFilesName(context, name));
            return;
        }

        if (type != null) {
            switch (type) {
                case AUDIO:
                    fileAdapter.addAll(FileUtil.getAudioLibrary(context));
                    break;

                case IMAGE:
                    fileAdapter.addAll(FileUtil.getImageLibrary(context));
                    break;

                case VIDEO:
                    fileAdapter.addAll(FileUtil.getVideoLibrary(context));
                    break;

                default:
                    break;
            }
            return;
        }
        setPath(FileUtil.getInternalStorage());
    }

    private void initializeComponents() {
        initAppBarLayout();
        initCoordinatorLayout();
        initDrawerLayout();
        initFloatingActionButton();
        initNavigationView();
        initRecyclerView();
        loadIntoRecyclerview();
        invalidateToolbar();
        invalidateTitle();
    }

    private void initCoordinatorLayout() {
        coordinatorLayout = findViewById(R.id.coordinator_layout);
    }

    private void initAppBarLayout() {
        toolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more));
        setSupportActionBar(toolbar);
    }

    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);

        if (drawerLayout == null) {
            return;
        }

        if (name != null || type != null) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void initFloatingActionButton() {
        fabCreate = findViewById(R.id.fabCreate);

        if (fabCreate == null) {
            return;
        }

        fabCreate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                actionCreate();
            }
        });

        if (name != null || type != null) {

            ViewGroup.LayoutParams layoutParams = fabCreate.getLayoutParams();

            ((CoordinatorLayout.LayoutParams) layoutParams).setAnchorId(View.NO_ID);

            fabCreate.setLayoutParams(layoutParams);

            fabCreate.hide();
        }
    }

    private void initNavigationView() {
        navigationView = findViewById(R.id.navigation_view);

        if (navigationView == null) {
            return;
        }

        MenuItem menuItem = navigationView.getMenu().findItem(R.id.navItemExternal);
        menuItem.setVisible(FileUtil.getExternalStorage() != null);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navItemAudio:
                        setType(AUDIO);
                        return true;

                    case R.id.navItemImage:
                        setType(IMAGE);
                        return true;

                    case R.id.navItemVideo:
                        setType(VIDEO);
                        return true;

                    case R.id.navItemFeedback:
                        goToFeedback();
                        return true;

                    case R.id.navItemSettings:
                        goToSettings();
                        return true;
                }

                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {

                    case R.id.navItemDirectory0:
                        setPath(FileUtil.getPublicDirectory(DCIM));
                        return true;

                    case R.id.navItemDirectory1:
                        setPath(FileUtil.getPublicDirectory(DOWNLOAD));
                        return true;

                    case R.id.navItemDirectory2:
                        setPath(FileUtil.getPublicDirectory(MOVIES));
                        return true;

                    case R.id.navItemDirectory3:
                        setPath(FileUtil.getPublicDirectory(MUSIC));
                        return true;

                    case R.id.navItemDirectory4:
                        setPath(FileUtil.getPublicDirectory(PICTURES));
                        return true;

                    default:
                        return true;
                }
            }
        });

        txtHeader = navigationView.getHeaderView(0).findViewById(R.id.txtHeader);
        txtHeader.setText(FileUtil.getStorageUsage(this));
        txtHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
            }
        });
    }

    private void initRecyclerView() {
        fileAdapter = new FileAdapter(this);
        fileAdapter.setOnItemClickListener(new OnItemClickListener(this));
        fileAdapter.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected() {
                invalidateOptionsMenu();
                invalidateTitle();
                invalidateToolbar();
            }
        });

        if (type != null) {
            switch (type) {
                case AUDIO:
                    fileAdapter.setItemLayout(R.layout.item_file_1);
                    fileAdapter.setSpanCount(getResources().getInteger(R.integer.span_count1));
                    break;

                case IMAGE:
                    fileAdapter.setItemLayout(R.layout.item_file_2);
                    fileAdapter.setSpanCount(getResources().getInteger(R.integer.span_count2));
                    break;

                case VIDEO:
                    fileAdapter.setItemLayout(R.layout.item_file_3);
                    fileAdapter.setSpanCount(getResources().getInteger(R.integer.span_count3));
                    break;

                default:
                    break;
            }
        } else {
            fileAdapter.setItemLayout(R.layout.item_file_0);
            fileAdapter.setSpanCount(getResources().getInteger(R.integer.span_count0));
        }

        rcvFile = findViewById(R.id.rcvFile);

        if (rcvFile != null) {
            rcvFile.setAdapter(fileAdapter);
        }
    }

    private void invalidateToolbar() {
        if (fileAdapter.anySelected()) {
            toolbar.setNavigationIcon(R.drawable.ic_clear);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fileAdapter.clearSelection();
                }
            });

        } else if (name == null && type == null) {
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawerLayout.openDrawer(navigationView);
                }
            });

        } else {
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        }
    }

    private void invalidateTitle() {
        if (fileAdapter.anySelected()) {
            int selectedItemCount = fileAdapter.getSelectedItemCount();
            toolbarLayout.setTitle(String.format("%s selected", selectedItemCount));

        } else if (name != null) {
            toolbarLayout.setTitle(String.format("Search for %s", name));

        } else if (type != null) {
            switch (type) {
                case AUDIO:
                    toolbarLayout.setTitle("Music");
                    break;

                case IMAGE:
                    toolbarLayout.setTitle("Images");
                    break;

                case VIDEO:
                    toolbarLayout.setTitle("Videos");
                    break;

                default:
                    break;
            }

        } else if (currentDirectory != null && !currentDirectory.equals(FileUtil.getInternalStorage())) {
            toolbarLayout.setTitle(FileUtil.getName(currentDirectory));

        } else {
            toolbarLayout.setTitle(getResources().getString(R.string.app_name));

        }
    }

    //----------------------------------------------------------------------------------------------

    private void actionCreate() {
        InputDialog inputDialog = new InputDialog(this, "Create", "Create directory") {

            @Override
            public void onActionClick(String text) {
                try {
                    File directory = FileUtil.createDirectory(currentDirectory, text);
                    fileAdapter.clearSelection();
                    fileAdapter.add(directory);

                } catch (Exception e) {
                    showMessage(e);
                }
            }
        };

        inputDialog.show();
    }

    private void actionDelete() {
        actionDelete(fileAdapter.getSelectedItems());
        fileAdapter.clearSelection();
    }

    private void actionDelete(final List<File> files) {
        final File sourceDirectory = currentDirectory;

        fileAdapter.removeAll(files);

        String message = String.format("%s files deleted", files.size());

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (currentDirectory == null || currentDirectory.equals(sourceDirectory)) {
                            fileAdapter.addAll(files);
                        }
                    }
                })
                .addCallback(new Snackbar.Callback() {

                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            try {
                                for (File file : files) {
                                    FileUtil.deleteFile(file);
                                }

                            } catch (Exception e) {
                                showMessage(e);
                            }
                        }

                        super.onDismissed(transientBottomBar, event);
                    }
                })
                .show();
    }

    private void actionRename() {
        final List<File> selectedItems = fileAdapter.getSelectedItems();

        InputDialog inputDialog = new InputDialog(this, "Rename", "Rename") {

            @Override
            public void onActionClick(String text) {
                fileAdapter.clearSelection();

                try {

                    if (selectedItems.size() == 1) {

                        File file = selectedItems.get(0);

                        int index = fileAdapter.indexOf(file);

                        fileAdapter.updateItemAt(index, FileUtil.renameFile(file, text));

                    } else {

                        int size = String.valueOf(selectedItems.size()).length();

                        String format = " (%0" + size + "d)";

                        for (int i = 0; i < selectedItems.size(); i++) {

                            File file = selectedItems.get(i);

                            int index = fileAdapter.indexOf(file);

                            File newFile = FileUtil.renameFile(file, text + String.format(format, i + 1));

                            fileAdapter.updateItemAt(index, newFile);
                        }
                    }
                } catch (Exception e) {

                    showMessage(e);
                }
            }
        };

        if (selectedItems.size() == 1) {

            inputDialog.setDefault(FileUtil.removeExtension(selectedItems.get(0).getName()));
        }

        inputDialog.show();
    }

    private void actionSearch() {
        InputDialog inputDialog = new InputDialog(this, "Search", "Search") {

            @Override
            public void onActionClick(String text) {
                setName(text);
            }
        };

        inputDialog.show();
    }

    private void actionCopy() {
        List<File> selectedItems = fileAdapter.getSelectedItems();
        fileAdapter.clearSelection();
        transferFiles(selectedItems, false);
    }

    private void actionMove() {
        List<File> selectedItems = fileAdapter.getSelectedItems();
        fileAdapter.clearSelection();
        transferFiles(selectedItems, true);
    }

    private void actionSend() {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        intent.setType("*/*");

        ArrayList<Uri> uris = new ArrayList<>();
        for (File file : fileAdapter.getSelectedItems()) {
            if (file.isFile()) {
                uris.add(Uri.fromFile(file));
            }
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        startActivity(intent);
    }

    private void actionSort() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        int checkedItem = PreferenceUtil.getInteger(this, "pref_sort", 0);

        String sorting[] = {"Name", "Last modified", "Size"};

        final Context context = this;

        builder.setSingleChoiceItems(sorting, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int criteria) {
                fileAdapter.update(criteria);

                PreferenceUtil.putInt(context, "pref_sort", criteria);

                dialog.dismiss();
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    private void transferFiles(final List<File> files, final Boolean delete) {

        String paste = delete ? "moved" : "copied";

        String message = String.format(Locale.getDefault(), "%d items waiting to be %s", files.size(), paste);

        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {

                    for (File file : files) {

                        fileAdapter.addAll(FileUtil.copyFile(file, currentDirectory));

                        if (delete) FileUtil.deleteFile(file);
                    }
                } catch (Exception e) {

                    showMessage(e);
                }
            }
        };


        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Paste", onClickListener)
                .show();
    }

    private void showMessage(Exception e) {

        showMessage(e.getMessage());
    }

    private void showMessage(String message) {

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    //----------------------------------------------------------------------------------------------

    private void goToFeedback() {

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary0));

        builder.build().launchUrl(this, Uri.parse("https://github.com/calintat/Explorer/issues"));
    }

    private void goToSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void goToApplicationSettings() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        intent.setData(Uri.fromParts("package", "com.nghiatv.filemanager", null));

        startActivity(intent);
    }

    private void setPath(File directory) {
        if (!directory.exists()) {

            Toast.makeText(this, "Directory doesn't exist", Toast.LENGTH_SHORT).show();

            return;
        }

        currentDirectory = directory;

        fileAdapter.clear();

        fileAdapter.clearSelection();

        fileAdapter.addAll(FileUtil.getChildren(directory));

        invalidateTitle();
    }

    private void setName(String name) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(EXTRA_NAME, name);

        startActivity(intent);
    }

    private void setType(String type) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(EXTRA_TYPE, type);

        if (Build.VERSION.SDK_INT >= 21) {

            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }

        startActivity(intent);
    }

    //----------------------------------------------------------------------------------------------

    private final class OnItemClickListener implements com.nghiatv.filemanager.view.adapter.OnItemClickListener {

        private final Context context;

        private OnItemClickListener(Context context) {

            this.context = context;
        }

        @Override
        public void onItemClick(int position) {

            final File file = fileAdapter.get(position);

            if (fileAdapter.anySelected()) {

                fileAdapter.toggle(position);

                return;
            }

            if (file.isDirectory()) {

                if (file.canRead()) {

                    setPath(file);
                } else {

                    showMessage("Cannot open directory");
                }
            } else {

                if (Intent.ACTION_GET_CONTENT.equals(getIntent().getAction())) {

                    Intent intent = new Intent();

                    intent.setDataAndType(Uri.fromFile(file), FileUtil.getMimeType(file));

                    setResult(Activity.RESULT_OK, intent);

                    finish();
                } else if (FileUtil.FileType.getFileType(file) == FileUtil.FileType.ZIP) {

                    final ProgressDialog dialog = ProgressDialog.show(context, "", "Unzipping", true);

                    /*Thread thread = new Thread(() -> {

                        try {

                            setPath(FileUtil.unzip(file));

                            runOnUiThread(dialog::dismiss);
                        } catch (Exception e) {

                            showMessage(e);
                        }
                    });

                    thread.run();*/
                } else {

                    try {

                        Intent intent = new Intent(Intent.ACTION_VIEW);

                        intent.setDataAndType(Uri.fromFile(file), FileUtil.getMimeType(file));

                        startActivity(intent);
                    } catch (Exception e) {

                        showMessage(String.format("Cannot open %s", FileUtil.getName(file)));
                    }
                }
            }
        }

        @Override
        public boolean onItemLongClick(int position) {

            fileAdapter.toggle(position);

            return true;
        }
    }

}
