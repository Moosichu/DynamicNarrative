/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Modified by James Riordan in Jan 2016, to remove references to other android classes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.os;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A mapping from String values to various types.
 */
@SuppressWarnings("unchecked")
/*
 * Compiler warns about three instances of unchecked casts, but all are
 * surrounded by try{...}catch(ClassCastException){...}
 */
public class BaseBundle implements Serializable {
    private static final long serialVersionUID = 1;

    static final int BUNDLE_MAGIC = 0x4C444E42; // 'B' 'N' 'D' 'L'

    HashMap<String, Object> mMap = null;

    /**
     * Constructs a new, empty Bundle.
     */
    public BaseBundle() {
        this(0);
    }

    /**
     * Constructs a new, empty Bundle sized to hold the given number of
     * elements. The Bundle will grow as needed.
     *
     * @param capacity
     *            the initial capacity of the Bundle
     */
    public BaseBundle(int capacity) {
        mMap = capacity > 0 ? new HashMap<String, Object>(capacity) : new HashMap<String, Object>();
    }

    /**
     * Constructs a Bundle containing a copy of the mappings from the given
     * Bundle.
     *
     * @param b
     *            a Bundle to be copied.
     */
    public BaseBundle(BaseBundle b) {
        if (b.mMap != null) {
            mMap = new HashMap<String, Object>(b.mMap);
        } else {
            mMap = null;
        }
    }

    public static BaseBundle deepcopy(BaseBundle b) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(buffer);
            oos.writeObject(b);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));

            return (BaseBundle) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Returns the number of mappings contained in this Bundle.
     *
     * @return the number of mappings as an int.
     */
    public int size() {

        return mMap.size();
    }

    /**
     * Returns true if the mapping of this Bundle is empty, false otherwise.
     */
    public boolean isEmpty() {

        return mMap.isEmpty();
    }

    /**
     * Removes all elements from the mapping of this Bundle.
     */
    public void clear() {

        mMap.clear();
    }

    /**
     * Returns true if the given key is contained in the mapping of this Bundle.
     *
     * @param key
     *            a String key
     * @return true if the key is part of the mapping, false otherwise
     */
    public boolean containsKey(String key) {

        return mMap.containsKey(key);
    }

    /**
     * Returns the entry with the given key as an object.
     *
     * @param key
     *            a String key
     * @return an Object, or null
     */
    public Object get(String key) {

        return mMap.get(key);
    }

    /**
     * Removes any entry with the given key from the mapping of this Bundle.
     *
     * @param key
     *            a String key
     */
    public void remove(String key) {

        mMap.remove(key);
    }

    /**
     * Inserts all mappings from the given Map into this BaseBundle.
     *
     * @param map
     *            a Map
     */
    public void putAll(Map<? extends String, ? extends Object> map) {
        mMap.putAll(map);
    }

    /**
     * Returns a Set containing the Strings used as keys in this Bundle.
     *
     * @return a Set of String keys
     */
    public Set<String> keySet() {

        return mMap.keySet();
    }

    /**
     * Returns a Set containing the Objects used as values in this Bundle.
     *
     * @return a Set of String keys
     */
    public Set<Object> valueSet() {

        return new HashSet<Object>(mMap.values());
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing any
     * existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a Boolean, or null
     */
    public void putBoolean(String key, boolean value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing any
     * existing value for the given key.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a byte
     */
    public void putByte(String key, byte value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing any
     * existing value for the given key.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a char, or null
     */
    public void putChar(String key, char value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing any
     * existing value for the given key.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a short
     */
    public void putShort(String key, short value) {

        mMap.put(key, value);
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing any
     * existing value for the given key.
     *
     * @param key
     *            a String, or null
     * @param value
     *            an int, or null
     */
    public void putInt(String key, int value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a long value into the mapping of this Bundle, replacing any
     * existing value for the given key.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a long
     */
    public void putLong(String key, long value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing any
     * existing value for the given key.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a float
     */
    public void putFloat(String key, float value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a double value into the mapping of this Bundle, replacing any
     * existing value for the given key.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a double
     */
    public void putDouble(String key, double value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a String value into the mapping of this Bundle, replacing any
     * existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a String, or null
     */
    public void putString(String key, String value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a CharSequence value into the mapping of this Bundle, replacing
     * any existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a CharSequence, or null
     */
    public void putCharSequence(String key, CharSequence value) {

        mMap.put(key, value);
    }

    /**
     * Inserts an ArrayList<Integer> value into the mapping of this Bundle,
     * replacing any existing value for the given key. Either key or value may
     * be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            an ArrayList<Integer> object, or null
     */
    public void putIntegerArrayList(String key, ArrayList<Integer> value) {

        mMap.put(key, value);
    }

    /**
     * Inserts an ArrayList<String> value into the mapping of this Bundle,
     * replacing any existing value for the given key. Either key or value may
     * be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            an ArrayList<String> object, or null
     */
    public void putStringArrayList(String key, ArrayList<String> value) {

        mMap.put(key, value);
    }

    /**
     * Inserts an ArrayList<CharSequence> value into the mapping of this Bundle,
     * replacing any existing value for the given key. Either key or value may
     * be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            an ArrayList<CharSequence> object, or null
     */
    public void putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a boolean array value into the mapping of this Bundle, replacing
     * any existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a boolean array object, or null
     */
    public void putBooleanArray(String key, boolean[] value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing any
     * existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a byte array object, or null
     */
    public void putByteArray(String key, byte[] value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a short array value into the mapping of this Bundle, replacing
     * any existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a short array object, or null
     */
    public void putShortArray(String key, short[] value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a char array value into the mapping of this Bundle, replacing any
     * existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a char array object, or null
     */
    public void putCharArray(String key, char[] value) {

        mMap.put(key, value);
    }

    /**
     * Inserts an int array value into the mapping of this Bundle, replacing any
     * existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            an int array object, or null
     */
    public void putIntArray(String key, int[] value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a long array value into the mapping of this Bundle, replacing any
     * existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a long array object, or null
     */
    public void putLongArray(String key, long[] value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a float array value into the mapping of this Bundle, replacing
     * any existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a float array object, or null
     */
    public void putFloatArray(String key, float[] value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a double array value into the mapping of this Bundle, replacing
     * any existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a double array object, or null
     */
    public void putDoubleArray(String key, double[] value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a String array value into the mapping of this Bundle, replacing
     * any existing value for the given key. Either key or value may be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a String array object, or null
     */
    public void putStringArray(String key, String[] value) {

        mMap.put(key, value);
    }

    /**
     * Inserts a CharSequence array value into the mapping of this Bundle,
     * replacing any existing value for the given key. Either key or value may
     * be null.
     *
     * @param key
     *            a String, or null
     * @param value
     *            a CharSequence array object, or null
     */
    public void putCharSequenceArray(String key, CharSequence[] value) {

        mMap.put(key, value);
    }

    /**
     * Returns the value associated with the given key, or false if no mapping
     * of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @return a boolean value
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    // Log a message if the value was non-null but not of the expected type
    void typeWarning(String key, Object value, String className, Object defaultValue, ClassCastException e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Key ");
        sb.append(key);
        sb.append(" expected ");
        sb.append(className);
        sb.append(" but value was a ");
        sb.append(value.getClass().getName());
        sb.append(".  The default value ");
        sb.append(defaultValue);
        sb.append(" was returned.");
        System.err.println(sb.toString());
        System.err.println("Attempt to cast generated internal exception:" + e.getMessage());
    }

    void typeWarning(String key, Object value, String className, ClassCastException e) {
        typeWarning(key, value, className, "<null>", e);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if no
     * mapping of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @param defaultValue
     *            Value to return if key does not exist
     * @return a boolean value
     */
    public boolean getBoolean(String key, boolean defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Boolean) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Boolean", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or (byte) 0 if no
     * mapping of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @return a byte value
     */
    public byte getByte(String key) {

        return getByte(key, (byte) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if no
     * mapping of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @param defaultValue
     *            Value to return if key does not exist
     * @return a byte value
     */

    public Byte getByte(String key, byte defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Byte) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Byte", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or (char) 0 if no
     * mapping of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @return a char value
     */
    public char getChar(String key) {

        return getChar(key, (char) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if no
     * mapping of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @param defaultValue
     *            Value to return if key does not exist
     * @return a char value
     */

    public char getChar(String key, char defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Character) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Character", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or (short) 0 if no
     * mapping of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @return a short value
     */
    public short getShort(String key) {

        return getShort(key, (short) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if no
     * mapping of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @param defaultValue
     *            Value to return if key does not exist
     * @return a short value
     */

    public short getShort(String key, short defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Short) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Short", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0 if no mapping of
     * the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @return an int value
     */
    public int getInt(String key) {

        return getInt(key, 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if no
     * mapping of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @param defaultValue
     *            Value to return if key does not exist
     * @return an int value
     */
    public int getInt(String key, int defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Integer) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Integer", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0L if no mapping of
     * the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @return a long value
     */
    public long getLong(String key) {

        return getLong(key, 0L);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if no
     * mapping of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @param defaultValue
     *            Value to return if key does not exist
     * @return a long value
     */
    public long getLong(String key, long defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Long) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Long", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0.0f if no mapping of
     * the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @return a float value
     */
    public float getFloat(String key) {

        return getFloat(key, 0.0f);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if no
     * mapping of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @param defaultValue
     *            Value to return if key does not exist
     * @return a float value
     */
    public float getFloat(String key, float defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Float) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Float", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0.0 if no mapping of
     * the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @return a double value
     */
    public double getDouble(String key) {

        return getDouble(key, 0.0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if no
     * mapping of the desired type exists for the given key.
     *
     * @param key
     *            a String
     * @param defaultValue
     *            Value to return if key does not exist
     * @return a double value
     */
    public double getDouble(String key, double defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Double) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Double", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return a String value, or null
     */
    public String getString(String key) {

        final Object o = mMap.get(key);
        try {
            return (String) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "String", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or defaultValue if no
     * mapping of the desired type exists for the given key or if a null value
     * is explicitly associated with the given key.
     *
     * @param key
     *            a String, or null
     * @param defaultValue
     *            Value to return if key does not exist or if a null value is
     *            associated with the given key.
     * @return the String value associated with the given key, or defaultValue
     *         if no valid String object is currently mapped to that key.
     */
    public String getString(String key, String defaultValue) {
        final String s = getString(key);
        return (s == null) ? defaultValue : s;
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return a CharSequence value, or null
     */
    public CharSequence getCharSequence(String key) {

        final Object o = mMap.get(key);
        try {
            return (CharSequence) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "CharSequence", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or defaultValue if no
     * mapping of the desired type exists for the given key or if a null value
     * is explicitly associated with the given key.
     *
     * @param key
     *            a String, or null
     * @param defaultValue
     *            Value to return if key does not exist or if a null value is
     *            associated with the given key.
     * @return the CharSequence value associated with the given key, or
     *         defaultValue if no valid CharSequence object is currently mapped
     *         to that key.
     */
    public CharSequence getCharSequence(String key, CharSequence defaultValue) {
        final CharSequence cs = getCharSequence(key);
        return (cs == null) ? defaultValue : cs;
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return an ArrayList<String> value, or null
     */
    public ArrayList<Integer> getIntegerArrayList(String key) {
        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (ArrayList<Integer>) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "ArrayList<Integer>", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return an ArrayList<String> value, or null
     */
    public ArrayList<String> getStringArrayList(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (ArrayList<String>) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "ArrayList<String>", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return an ArrayList<CharSequence> value, or null
     */
    public ArrayList<CharSequence> getCharSequenceArrayList(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (ArrayList<CharSequence>) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "ArrayList<CharSequence>", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return a boolean[] value, or null
     */
    public boolean[] getBooleanArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (boolean[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "byte[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return a byte[] value, or null
     */
    public byte[] getByteArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (byte[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "byte[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return a short[] value, or null
     */
    public short[] getShortArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (short[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "short[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return a char[] value, or null
     */
    public char[] getCharArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (char[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "char[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return an int[] value, or null
     */
    public int[] getIntArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (int[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "int[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return a long[] value, or null
     */
    public long[] getLongArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (long[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "long[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return a float[] value, or null
     */
    public float[] getFloatArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (float[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "float[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return a double[] value, or null
     */
    public double[] getDoubleArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (double[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "double[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return a String[] value, or null
     */
    public String[] getStringArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (String[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "String[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of
     * the desired type exists for the given key or a null value is explicitly
     * associated with the key.
     *
     * @param key
     *            a String, or null
     * @return a CharSequence[] value, or null
     */
    public CharSequence[] getCharSequenceArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (CharSequence[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "CharSequence[]", e);
            return null;
        }
    }
}
