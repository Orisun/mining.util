package com.orisun.mining.util.common.base;

import javax.annotation.Nullable;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;
/**
 * Helper functions that operate on any {@code Object}, and are not already provided in
 * {@link java.util.Objects}.
 *
 * @Author: Laurence Gonsalves, Fido
 * @Date: 24/2/2017
 * @Version: 1.1
 */
public final class Objects {

    public static <T> T firstNonNull(@Nullable T first, @Nullable T second) {
        return first != null ? first : checkNotNull(second);
    }

    /**
     * Creates an instance of {@link ToStringHelper}.
     *
     * <p>This is helpful for implementing {@link Object#toString()}. Specification by example:
     *
     * <pre>   {@code
     *   // Returns "ClassName{}"
     *   MoreObjects.toStringHelper(this)
     *       .toString();
     *
     *   // Returns "ClassName{x=1}"
     *   MoreObjects.toStringHelper(this)
     *       .add("x", 1)
     *       .toString();
     *
     *   // Returns "MyObject{x=1}"
     *   MoreObjects.toStringHelper("MyObject")
     *       .add("x", 1)
     *       .toString();
     *
     *   // Returns "ClassName{x=1, y=foo}"
     *   MoreObjects.toStringHelper(this)
     *       .add("x", 1)
     *       .add("y", "foo")
     *       .toString();
     *
     *   // Returns "ClassName{x=1}"
     *   MoreObjects.toStringHelper(this)
     *       .omitNullValues()
     *       .add("x", 1)
     *       .add("y", null)
     *       .toString();
     *   }}</pre>
     *
     *
     * @param self the object to generate the string for (typically {@code this}), used only for its
     *     class name
     * @since 1.0
     */
    public static ToStringHelper toStringHelper(Object self) {
        return new ToStringHelper(self.getClass().getSimpleName());
    }

    /**
     * Creates an instance of {@link ToStringHelper} in the same manner as
     * {@link #toStringHelper(Object)}, but using the simple name of {@code clazz} instead of using an
     * instance's {@link Object#getClass()}.
     *
     * <p>Note that in GWT, class names are often obfuscated.
     *
     * @param clazz the {@link Class} of the instance
     */
    public static ToStringHelper toStringHelper(Class<?> clazz) {
        return new ToStringHelper(clazz.getSimpleName());
    }

    /**
     * Creates an instance of {@link ToStringHelper} in the same manner as
     * {@link #toStringHelper(Object)}, but using {@code className} instead of using an instance's
     * {@link Object#getClass()}.
     *
     * @param className the name of the instance type
     * @since 1.0
     */
    public static ToStringHelper toStringHelper(String className) {
        return new ToStringHelper(className);
    }

    /**
     * Support class for {@link Objects#toStringHelper}.
     *
     * @author Jason Lee
     */
    public static final class ToStringHelper {
        private final String className;
        private final ValueHolder holderHead = new ValueHolder();
        private ValueHolder holderTail = holderHead;
        private boolean omitNullValues = false;

        /**
         * Use {@link Objects#toStringHelper(Object)} to create an instance.
         */
        private ToStringHelper(String className) {
            this.className = checkNotNull(className);
        }


        /**
         * Configures the {@link ToStringHelper} so {@link #toString()} will ignore properties with null
         * value. The order of calling this method, relative to the {@code add()}/{@code addValue()}
         * methods, is not significant.
         *
         * the return value of the method can be safely ignored.
         */
        public ToStringHelper omitNullValues() {
            omitNullValues = true;
            return this;
        }

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format. If {@code value}
         * is {@code null}, the string {@code "null"} is used, unless {@link #omitNullValues()} is
         * called, in which case this name/value pair will not be added.
         *
         * the return value of the method can be safely ignored.
         */
        public ToStringHelper add(String name, @Nullable Object value) {
            return addHolder(name, value);
        }

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * the return value of the method can be safely ignored.
         */
        public ToStringHelper add(String name, boolean value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * the return value of the method can be safely ignored.
         */
        public ToStringHelper add(String name, char value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * the return value of the method can be safely ignored.
         */
        public ToStringHelper add(String name, double value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * the return value of the method can be safely ignored.
         */
        public ToStringHelper add(String name, float value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * the return value of the method can be safely ignored.
         */
        public ToStringHelper add(String name, int value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * the return value of the method can be safely ignored.
         */
        public ToStringHelper add(String name, long value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>It is strongly encouraged to use {@link #add(String, long)} instead and give value a
         * readable name.
         *
         * the return value of the method can be safely ignored.
         *
         */
        public ToStringHelper addValue(@Nullable Object value) {
            return addHolder(value);
        }

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>It is strongly encouraged to use {@link #add(String, long)} instead and give value a
         * readable name.
         *
         * the return value of the method can be safely ignored.
         *
         */
        public ToStringHelper addValue(boolean value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>It is strongly encouraged to use {@link #add(String, long)} instead and give value a
         * readable name.
         *
         * the return value of the method can be safely ignored.
         *
         */
        public ToStringHelper addValue(char value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>It is strongly encouraged to use {@link #add(String, long)} instead and give value a
         * readable name.
         *
         * the return value of the method can be safely ignored.
         *
         */
        public ToStringHelper addValue(double value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>It is strongly encouraged to use {@link #add(String, long)} instead and give value a
         * readable name.
         *
         * the return value of the method can be safely ignored.
         *
         */
        public ToStringHelper addValue(float value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>It is strongly encouraged to use {@link #add(String, long)} instead and give value a
         * readable name.
         *
         * the return value of the method can be safely ignored.
         */
        public ToStringHelper addValue(int value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>It is strongly encouraged to use {@link #add(String, long)} instead and give value a
         * readable name.
         *
         * the return value of the method can be safely ignored.
         */
        public ToStringHelper addValue(long value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * Returns a string in the format specified by {@link Objects#toStringHelper(Object)}.
         *
         * <p>After calling this method, you can keep adding more properties to later call toString()
         * again and get a more complete representation of the same object; but properties cannot be
         * removed, so this only allows limited reuse of the helper instance. The helper allows
         * duplication of properties (multiple name/value pairs with the same name can be added).
         */
        @Override
        public String toString() {
            // create a copy to keep it consistent in case value changes
            boolean omitNullValuesSnapshot = omitNullValues;
            String nextSeparator = "";
            StringBuilder builder = new StringBuilder(32).append(className).append('{');
            for (ValueHolder valueHolder = holderHead.next;
                 valueHolder != null;
                 valueHolder = valueHolder.next) {
                Object value = valueHolder.value;
                if (!omitNullValuesSnapshot || value != null) {
                    builder.append(nextSeparator);
                    nextSeparator = ", ";

                    if (valueHolder.name != null) {
                        builder.append(valueHolder.name).append('=');
                    }
                    if (value != null && value.getClass().isArray()) {
                        Object[] objectArray = {value};
                        String arrayString = Arrays.deepToString(objectArray);
                        builder.append(arrayString, 1, arrayString.length() - 1);
                    } else {
                        builder.append(value);
                    }
                }
            }
            return builder.append('}').toString();
        }

        private ValueHolder addHolder() {
            ValueHolder valueHolder = new ValueHolder();
            holderTail = holderTail.next = valueHolder;
            return valueHolder;
        }

        private ToStringHelper addHolder(@Nullable Object value) {
            ValueHolder valueHolder = addHolder();
            valueHolder.value = value;
            return this;
        }

        private ToStringHelper addHolder(String name, @Nullable Object value) {
            ValueHolder valueHolder = addHolder();
            valueHolder.value = value;
            valueHolder.name = checkNotNull(name);
            return this;
        }

        private static final class ValueHolder {
            String name;
            Object value;
            ValueHolder next;
        }
    }
}
