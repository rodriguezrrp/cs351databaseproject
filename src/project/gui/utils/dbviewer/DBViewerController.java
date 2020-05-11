package project.gui.utils.dbviewer;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DBViewerController {

    private Collection<? extends Node> leftToolbarItems;
    private Collection<? extends Node> rightToolbarItems;
    private SortedList<List<Object>> tableItemList;

    // the fxml elements with ids
    @FXML
    private TableView<List<Object>> tableView;
    @FXML
    private ToolBar topToolBar;

    public DBViewerController() {
        this(null, null);
    }

    public DBViewerController(Collection<? extends Node> leftToolbarItems,
                              Collection<? extends Node> rightToolbarItems) {
        this.leftToolbarItems = leftToolbarItems;
        this.rightToolbarItems = rightToolbarItems;
    }

    @FXML
    private void initialize() {
        // setup the toolbar (won't do anything if the lists are empty)
        setupToolbar(this.leftToolbarItems, this.rightToolbarItems);
    }

    public void setupToolbar(Collection<? extends Node> leftToolbarItems,
                             Collection<? extends Node> rightToolbarItems) {
        topToolBar.getItems().clear();
        // fill in the left items
        if (leftToolbarItems != null) {
            topToolBar.getItems().addAll(leftToolbarItems);
        }
        // fill in the right items (adding the spacer if so)
        if (rightToolbarItems != null
                && rightToolbarItems.size() > 0) {
            // setup a spacer to shove the right toolbar buttons over to the right
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            topToolBar.getItems().add(spacer);
            topToolBar.getItems().addAll(rightToolbarItems);
        }
    }

    public void fillTable(ResultSet rset) {
        setupTableColumns(rset);
        setTableItems(rset);
//        tableView.getScene().getRoot().getProperties()
    }

    public void setupTableColumns(ResultSet rset) {
        // setup the table's columns
        ResultSetMetaData metaData;
        int columnCount = 0;
        try {
            metaData = rset.getMetaData();
            columnCount = metaData.getColumnCount();
        } catch (SQLException e) {
            System.err.println("Fatal error occurred when getting column information!");
            e.printStackTrace();
            return; // just exit out of this method
        }
        tableView.getColumns().clear();
        for(int ind=1; ind<=columnCount; ind++) {
            String columnLabel = "";
            try {
                columnLabel = metaData.getColumnLabel(ind);
            } catch (SQLException e) {
                System.err.println("An error occurred retrieving the label of column "
                        + ind + ", using default string \""+columnLabel+"\" instead!");
                e.printStackTrace();
            }
            TableColumn<List<Object>,String> tableCol = new TableColumn<>(columnLabel);
            tableCol.setCellValueFactory(new DataValueFactory(ind));
            tableView.getColumns().add(tableCol);
        }
    }

    public void setTableItems(ResultSet rset) {
        int columnCount = 0;
        if(rset != null) {
            try {
                columnCount = rset.getMetaData().getColumnCount();
            } catch (SQLException e) {
                System.err.println("Fatal error occurred when setting table items!");
                e.printStackTrace();
                return; // just exit out of this method
            }
        }
        if(columnCount <= 0) {
            // clear table
            tableView.setItems(null);
            // clear references to (hopefully) prevent memory leaks
            if(tableItemList != null) {
                tableItemList.comparatorProperty().unbind();
                tableItemList = null;
            }
        } else {
            // get the new data
            List<List<Object>> rowsData = new ArrayList<>();
            while(true) {
                try {
                    // if there's no more data, stop the loop
                    if (!rset.next()) break;
                } catch (SQLException e) {
                    System.err.println("An error occurred when getting next row!" +
                            " Exiting loop and using what data is currently had.");
                    e.printStackTrace();
                    break; // also stop the loop if an error occurs
                }
                // get data out of current row
                List<Object> rowData = new ArrayList<>(columnCount);
                //   note that the indices start from 1, not 0!
                for(int ind=1; ind<=columnCount; ind++) {
                    try {
                        // get the row's value for the current column
                        final Object value = rset.getObject(ind);
                        // stick it into our data
                        rowData.add(value);
                    } catch (SQLException e) {
                        System.err.println("Getting result set's object at index " + ind + " failed!");
                        e.printStackTrace();
                    }
                }
                rowsData.add(rowData);
            }
            // fill out table with the new data
            if (tableItemList != null) {
                // remove the linkage from the previous item list
                tableItemList.comparatorProperty().unbind();
            }
            tableItemList = new SortedList<>(FXCollections.observableArrayList(rowsData));
            tableView.setItems(tableItemList);
            tableItemList.comparatorProperty().bind(tableView.comparatorProperty());
        }
    }


    //////////////////////////////////

    static class DataValueFactory implements Callback<TableColumn.CellDataFeatures<List<Object>,String>, ObservableValue<String>> {
        private final int index;
        public DataValueFactory(int index) {
            this.index = index;
        }
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<List<Object>,String> features) {
            final List<Object> columnData = features.getValue();
            // note index-1, because ResultSet's column indexing starts from 1, not 0,
            // and this must be translated into the standard indexing for Java's Lists
            final Object value = columnData.get(index-1);

            try {
                SimpleStringProperty temp = new SimpleStringProperty(value.toString());
                return temp;
            }catch (Exception e){
                e.printStackTrace();
                return new SimpleStringProperty("NULL");
            }

            //return new SimpleStringProperty(value.toString());
            // As a side note: the ObservableValue returned from this method actually is not observing anything,
            // because a new SimpleStringProperty is getting created each time this is called,
            // and its value is not bound to anything.
            // Therefore, any changes to the result set will not automatically update this cell value.

        }
    }

}
