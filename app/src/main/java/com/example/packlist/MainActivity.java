
package com.example.packlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class MainActivity extends Activity {

    DatabaseHelper database;

    LinearLayout mainLayout, itemLayout;

    EditText itemInput, categoryInput, searchInput;

    Spinner filterSpinner;

    TextView progressText, percentageText;

    ProgressBar progressBar;

    ArrayList<String> filterOptions = new ArrayList<>();

    ArrayAdapter<String> filterAdapter;

    String currentFilter = "All";

    int blue = Color.rgb(54, 95, 230);
    int dark = Color.rgb(31, 43, 75);
    int purple = Color.rgb(119, 85, 220);
    int grey = Color.rgb(110, 120, 140);

    String[] defaultCategories = {
            "Clothes", "Documents", "Gadgets"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = new DatabaseHelper(this);

        createInterface();
        displayItems();
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(dp(radius));
        return bg;
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    private Button makeButton(String value, int bgColor) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setPadding(dp(10), 0, dp(10), 0);
        b.setBackground(rounded(bgColor, 12));
        return b;
    }

    private LinearLayout card() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(15), dp(16), dp(15));
        box.setBackground(rounded(Color.WHITE, 18));
        box.setElevation(dp(3));
        return box;
    }

    private void createInterface() {

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(244, 246, 252));

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(dp(18), dp(15), dp(18), dp(25));

        scroll.addView(mainLayout);

        // HEADER

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(18), dp(20), dp(12), dp(20));
        header.setBackground(rounded(dark, 20));

        LinearLayout headerText = new LinearLayout(this);
        headerText.setOrientation(LinearLayout.VERTICAL);

        TextView title = text("🎒 PackList", 27, Color.WHITE);
        title.setTypeface(null, Typeface.BOLD);

        TextView subtitle = text(
                "Pack smart. Travel happy!",
                13,
                Color.rgb(220, 225, 245)
        );

        headerText.addView(title);
        headerText.addView(subtitle);

        header.addView(headerText,
                new LinearLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
                ));

        Button menuButton = makeButton("⋮", purple);
        header.addView(menuButton);

        menuButton.setOnClickListener(v -> showMenu(v));

        mainLayout.addView(header);

        // PROGRESS CARD

        LinearLayout progressCard = card();

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1, -2);

        cardParams.setMargins(0, dp(18), 0, dp(18));
        progressCard.setLayoutParams(cardParams);

        TextView progressHeading = text(
                "Your Packing Progress", 18, dark
        );

        progressHeading.setTypeface(null, Typeface.BOLD);

        progressCard.addView(progressHeading);

        LinearLayout progressRow = new LinearLayout(this);
        progressRow.setGravity(Gravity.CENTER_VERTICAL);
        progressRow.setPadding(0, dp(12), 0, dp(8));

        progressText = text("0 / 0 items packed", 14, grey);

        percentageText = text("0%", 20, blue);
        percentageText.setTypeface(null, Typeface.BOLD);

        progressRow.addView(progressText,
                new LinearLayout.LayoutParams(0, -2, 1));

        progressRow.addView(percentageText);

        progressCard.addView(progressRow);

        progressBar = new ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
        );

        progressBar.setMax(100);
        progressBar.setProgress(0);

        progressBar.setProgressTintList(
                android.content.res.ColorStateList.valueOf(blue)
        );

        progressCard.addView(progressBar,
                new LinearLayout.LayoutParams(-1, dp(10)));

        mainLayout.addView(progressCard);

        // ADD ITEM CARD

        LinearLayout addCard = card();

        TextView addHeading = text(
                "➕ Add New Item", 19, dark
        );

        addHeading.setTypeface(null, Typeface.BOLD);

        addCard.addView(addHeading);

        itemInput = new EditText(this);
        itemInput.setSingleLine(true);
        itemInput.setTextSize(15);
        itemInput.setHint("Enter item name");

        itemInput.setBackground(rounded(
                Color.rgb(244, 246, 250), 12
        ));

        itemInput.setPadding(
                dp(14), dp(12), dp(14), dp(12)
        );

        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(-1, dp(52));

        inputParams.setMargins(0, dp(15), 0, dp(10));

        addCard.addView(itemInput, inputParams);

        categoryInput = new EditText(this);
        categoryInput.setSingleLine(true);
        categoryInput.setTextSize(15);
        categoryInput.setHint("Enter category (e.g. Medicines)");

        categoryInput.setBackground(rounded(
                Color.rgb(244, 246, 250), 12
        ));

        categoryInput.setPadding(
                dp(14), dp(12), dp(14), dp(12)
        );

        LinearLayout.LayoutParams categoryParams =
                new LinearLayout.LayoutParams(-1, dp(52));

        categoryParams.setMargins(0, dp(5), 0, dp(5));

        addCard.addView(categoryInput, categoryParams);

        Button addButton = makeButton(
                "＋  ADD ITEM", blue
        );

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, dp(48));

        buttonParams.setMargins(0, dp(15), 0, 0);

        addCard.addView(addButton, buttonParams);

        addButton.setOnClickListener(v -> addNewItem());

        LinearLayout.LayoutParams addCardParams =
                new LinearLayout.LayoutParams(-1, -2);

        addCardParams.setMargins(0, 0, 0, dp(20));

        addCard.setLayoutParams(addCardParams);

        mainLayout.addView(addCard);

        // CHECKLIST HEADING

        TextView listHeading = text(
                "My Packing Checklist", 21, dark
        );

        listHeading.setTypeface(null, Typeface.BOLD);

        mainLayout.addView(listHeading);

        // SEARCH

        searchInput = new EditText(this);

        searchInput.setHint("Search packing items...");
        searchInput.setSingleLine(true);
        searchInput.setTextSize(14);

        searchInput.setPadding(
                dp(14), dp(10), dp(14), dp(10)
        );

        searchInput.setBackground(rounded(Color.WHITE, 14));

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(-1, dp(50));

        searchParams.setMargins(0, dp(12), 0, dp(10));

        mainLayout.addView(searchInput, searchParams);

        // CATEGORY FILTER

        filterSpinner = new Spinner(this);

        filterAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filterOptions
        );

        filterAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        filterSpinner.setAdapter(filterAdapter);

        mainLayout.addView(filterSpinner);

        filterSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (position < filterOptions.size()) {
                            currentFilter =
                                    filterOptions.get(position);
                            displayItems();
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );

        // SEARCH LISTENER

        searchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start,
                    int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start,
                    int before, int count) {

                displayItems();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // ITEMS LIST

        itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams listParams =
                new LinearLayout.LayoutParams(-1, -2);

        listParams.setMargins(0, dp(15), 0, 0);

        mainLayout.addView(itemLayout, listParams);

        setContentView(scroll);
    }

    // ADD NEW ITEM

    private void addNewItem() {

        String name = itemInput.getText()
                .toString().trim();

        String category = categoryInput.getText()
                .toString().trim();

        if (name.isEmpty()) {
            itemInput.setError("Please enter item name");
            return;
        }

        if (category.isEmpty()) {
            categoryInput.setError("Please enter a category");
            return;
        }

        database.addItem(name, category);

        itemInput.setText("");
        categoryInput.setText("");

        currentFilter = "All";

        refreshFilterOptions();
        displayItems();

        Toast.makeText(this,
                "Item added successfully!",
                Toast.LENGTH_SHORT).show();
    }

    // REFRESH CATEGORY FILTER

    private void refreshFilterOptions() {

        ArrayList<String> savedCategories =
                database.getAllCategories();

        if (!currentFilter.equals("All") &&
                !savedCategories.contains(currentFilter)) {

            currentFilter = "All";
        }

        filterOptions.clear();
        filterOptions.add("All");
        filterOptions.addAll(savedCategories);

        filterAdapter.notifyDataSetChanged();

        int position = filterOptions.indexOf(currentFilter);

        if (position < 0) {
            position = 0;
        }

        if (filterSpinner.getSelectedItemPosition() != position) {
            filterSpinner.setSelection(position);
        }
    }

    // DISPLAY ITEMS

    private void displayItems() {

        if (itemLayout == null) return;

        itemLayout.removeAllViews();

        refreshFilterOptions();

        ArrayList<DatabaseHelper.Item> items =
                database.getAllItems();

        int total = items.size();
        int packed = 0;

        for (DatabaseHelper.Item item : items) {
            if (item.packed) {
                packed++;
            }
        }

        int percent = total == 0 ? 0 :
                (packed * 100 / total);

        progressText.setText(
                packed + " / " + total + " items packed"
        );

        percentageText.setText(percent + "%");

        progressBar.setProgress(percent);

        String search = searchInput.getText()
                .toString().trim().toLowerCase();

        int shown = 0;

        for (DatabaseHelper.Item item : items) {

            if (!currentFilter.equals("All") &&
                    !item.category.equals(currentFilter)) {
                continue;
            }

            if (!item.name.toLowerCase().contains(search)) {
                continue;
            }

            shown++;

            // ITEM CARD

            LinearLayout itemCard = card();

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(-1, -2);

            params.setMargins(0, 0, 0, dp(12));

            itemCard.setLayoutParams(params);

            LinearLayout row = new LinearLayout(this);

            row.setGravity(Gravity.CENTER_VERTICAL);

            CheckBox checkBox = new CheckBox(this);

            checkBox.setText(item.name);
            checkBox.setTextSize(16);
            checkBox.setTextColor(dark);
            checkBox.setTypeface(null, Typeface.BOLD);
            checkBox.setChecked(item.packed);
            checkBox.setPadding(0, 0, dp(5), 0);

            row.addView(checkBox,
                    new LinearLayout.LayoutParams(0, -2, 1));

            itemCard.addView(row);

            TextView category = text(
                    "●  " + item.category, 13, purple
            );

            category.setPadding(
                    dp(35), dp(5), 0, dp(12)
            );

            itemCard.addView(category);

            // EDIT AND DELETE BUTTONS

            LinearLayout actions = new LinearLayout(this);

            actions.setGravity(Gravity.END);

            Button editButton = makeButton(
                    "✎  Edit", blue
            );

            Button deleteButton = makeButton(
                    "Delete", Color.rgb(220, 65, 75)
            );

            LinearLayout.LayoutParams actionParams =
                    new LinearLayout.LayoutParams(-2, dp(42));

            actionParams.setMargins(dp(5), 0, dp(5), 0);

            actions.addView(editButton, actionParams);
            actions.addView(deleteButton, actionParams);

            itemCard.addView(actions);

            itemLayout.addView(itemCard);

            // CHECKBOX LISTENER

            checkBox.setOnCheckedChangeListener(
                    (button, checked) -> {

                        database.updatePacked(item.id, checked);

                        displayItems();
                    }
            );

            // EDIT LISTENER

            editButton.setOnClickListener(
                    v -> editItem(item)
            );

            // DELETE LISTENER

            deleteButton.setOnClickListener(
                    v -> confirmDelete(item)
            );
        }

        if (shown == 0) {

            TextView empty = text(
                    total == 0 ?
                            "🎒 Your packing list is empty.\nAdd your first item above!" :
                            "No matching items found.",
                    16, grey
            );

            empty.setGravity(Gravity.CENTER);

            empty.setPadding(
                    0, dp(30), 0, dp(30)
            );

            itemLayout.addView(empty);
        }
    }

    // EDIT ITEM

    private void editItem(DatabaseHelper.Item item) {

        EditText nameInput = new EditText(this);

        nameInput.setSingleLine(true);
        nameInput.setText(item.name);
        nameInput.setHint("Item name");

        EditText categoryEdit = new EditText(this);

        categoryEdit.setSingleLine(true);
        categoryEdit.setText(item.category);
        categoryEdit.setHint("Enter category");

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setPadding(
                dp(20), dp(10), dp(20), dp(10)
        );

        layout.addView(nameInput);
        layout.addView(categoryEdit);

        new AlertDialog.Builder(this)
                .setTitle("✏ Edit Packing Item")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {

                    String newName = nameInput.getText()
                            .toString().trim();

                    String newCategory = categoryEdit.getText()
                            .toString().trim();

                    if (newName.isEmpty()) {

                        Toast.makeText(this,
                                "Item name cannot be empty",
                                Toast.LENGTH_SHORT).show();

                        return;
                    }

                    if (newCategory.isEmpty()) {

                        Toast.makeText(this,
                                "Please enter a category",
                                Toast.LENGTH_SHORT).show();

                        return;
                    }

                    database.updateItem(
                            item.id,
                            newName,
                            newCategory
                    );

                    refreshFilterOptions();
                    displayItems();

                    Toast.makeText(this,
                            "Item updated!",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // DELETE SINGLE ITEM

    private void confirmDelete(DatabaseHelper.Item item) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Item?")
                .setMessage(
                        "Are you sure you want to delete \"" +
                                item.name + "\"?"
                )
                .setPositiveButton("Delete", (dialog, which) -> {

                    database.deleteItem(item.id);

                    refreshFilterOptions();
                    displayItems();

                    Toast.makeText(this,
                            "Item deleted successfully!",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // THREE DOT MENU

    private void showMenu(View view) {

        PopupMenu popup = new PopupMenu(this, view);

        popup.getMenu().add("Uncheck All");
        popup.getMenu().add("Delete Packed Items");
        popup.getMenu().add("Add Default Items");
        popup.getMenu().add("Delete All Items");

        popup.setOnMenuItemClickListener(menuItem -> {

            String choice = menuItem.getTitle().toString();

            if (choice.equals("Uncheck All")) {

                database.uncheckAll();

                displayItems();

                Toast.makeText(this,
                        "All items unchecked",
                        Toast.LENGTH_SHORT).show();

            } else if (choice.equals("Delete Packed Items")) {

                new AlertDialog.Builder(this)
                        .setTitle("Delete Packed Items?")
                        .setMessage(
                                "All checked items will be deleted."
                        )
                        .setPositiveButton("Delete", (d, w) -> {

                            database.deletePackedItems();

                            refreshFilterOptions();
                            displayItems();

                            Toast.makeText(this,
                                    "Packed items deleted",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            } else if (choice.equals("Add Default Items")) {

                addDefaultItems();

                currentFilter = "All";

                refreshFilterOptions();
                displayItems();

                Toast.makeText(this,
                        "Default items added",
                        Toast.LENGTH_SHORT).show();

            } else if (choice.equals("Delete All Items")) {

                new AlertDialog.Builder(this)
                        .setTitle("Delete Everything?")
                        .setMessage(
                                "This will permanently delete all packing items."
                        )
                        .setPositiveButton("Delete All", (d, w) -> {

                            ArrayList<DatabaseHelper.Item> items =
                                    database.getAllItems();

                            for (DatabaseHelper.Item item : items) {
                                database.deleteItem(item.id);
                            }

                            currentFilter = "All";

                            refreshFilterOptions();
                            displayItems();

                            Toast.makeText(this,
                                    "All items deleted",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            return true;
        });

        popup.show();
    }

    // DEFAULT ITEMS

    private void addDefaultItems() {

        database.addItem("T-Shirts", "Clothes");
        database.addItem("Jeans", "Clothes");
        database.addItem("Jacket", "Clothes");
        database.addItem("Socks", "Clothes");

        database.addItem("Passport", "Documents");
        database.addItem("ID Card", "Documents");
        database.addItem("Tickets", "Documents");

        database.addItem("Mobile Phone", "Gadgets");
        database.addItem("Charger", "Gadgets");
        database.addItem("Headphones", "Gadgets");
    }
}
