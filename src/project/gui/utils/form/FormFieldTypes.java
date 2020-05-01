package project.gui.utils.form;

import java.util.List;
import java.util.Objects;

public class FormFieldTypes<V> {

    public static final FormFieldTypes<String> TEXT = new FormFieldTypes<>(); //new FormFieldTypes<>(String.class);
    public static final FormFieldTypes<List<String>> DROPDOWN = new FormFieldTypes<>(); //new FormFieldTypes<>(List.class);

    private final int ID;
    private static int IDs = 0;

    private FormFieldTypes() {
        this.ID = IDs;
        IDs++;
    }

//    private final Class<? extends V> valueClass;
//
//    private FormFieldTypes(Class<? extends V> valueClass) {
//        this.valueClass = valueClass;
//    }

//    public Class<? extends V> getValueClass() {
//        return valueClass;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormFieldTypes<?> that = (FormFieldTypes<?>) o;
        return ID == that.ID;
    }

    @Override
    public int hashCode() {
//        return Objects.hash(ID);
        return ID;
    }

}
