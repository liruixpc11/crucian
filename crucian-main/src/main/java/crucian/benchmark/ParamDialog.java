package crucian.benchmark;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LiRuiNet
 *         14-2-28 下午5:42
 */
public class ParamDialog {
    private static class ValueHolder {
        JComponent component;
        Class<?> type;
        Object value;
        String defaultValue;

        private ValueHolder(Class<?> type) {
            this.type = type;
        }

        private ValueHolder(JComponent component, Class<?> type) {
            this.component = component;
            this.type = type;
        }
    }

    private final List<ParamInfo> paramInfoList = new ArrayList<ParamInfo>();
    private final Map<String, ValueHolder> valueHolderMap = new HashMap<String, ValueHolder>();
    private Frame parent;
    private String name;
    private String type;

    public ParamDialog(Frame parent, List<ParamInfo> paramInfoList, String name, String type) {
        this.parent = parent;
        this.paramInfoList.addAll(paramInfoList);
        this.name = name;
        this.type = type;
    }

    /**
     * 显示对话框
     *
     * @return true 按下确定按钮；false 按下取消按钮
     */
    public boolean show() {
        return paramInfoList.isEmpty() ||
                JOptionPane.showConfirmDialog(parent, createPanel(), name + "(" + type + ")参数", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }

    public Object[] getValues() {
        Object[] values = new Object[paramInfoList.size()];
        for (int i = 0; i < paramInfoList.size(); i++) {
            values[i] = setValue(valueHolderMap.get(paramInfoList.get(i).getName()));
        }
        return values;
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 10));
        for (ParamInfo paramInfo : paramInfoList) {
            panel.add(new JLabel(paramInfo.getName()));
            ValueHolder valueHolder = createValueHolder(paramInfo);
            panel.add(valueHolder.component);
            valueHolderMap.put(paramInfo.getName(), valueHolder);
        }
        return panel;
    }

    private ValueHolder createValueHolder(final ParamInfo paramInfo) {
        final Class<?> type = paramInfo.getType();
        if (type == Boolean.class || type == boolean.class) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.getModel().setSelected(Boolean.parseBoolean(paramInfo.getDefaultValue()));
            return new ValueHolder(checkBox, type);
        } else {
            return new ValueHolder(new JTextField(paramInfo.getDefaultValue()), type);
        }
    }

    private Object setValue(ValueHolder valueHolder) {
        Class<?> type = valueHolder.type;

        String text = valueHolder.defaultValue;
        if (valueHolder.component.getClass() == JTextField.class) {
            text = ((JTextField) valueHolder.component).getText();
        } else if (valueHolder.component.getClass() == JCheckBox.class) {
            valueHolder.value = ((JCheckBox) valueHolder.component).getModel().isSelected();
        }

        if (text != null && text.trim().length() != 0) {
            try {
                if (type.equals(Integer.class) || type == int.class) {
                    valueHolder.value = Integer.parseInt(text);
                } else if (type == Short.class || type == short.class) {
                    valueHolder.value = Short.parseShort(text);
                } else if (type == Byte.class || type == byte.class) {
                    valueHolder.value = Byte.parseByte(text);
                } else if (type == Float.class || type == float.class) {
                    valueHolder.value = Float.parseFloat(text);
                } else if (type == Double.class || type == double.class) {
                    valueHolder.value = Double.parseDouble(text);
                } else if (type == Boolean.class || type == boolean.class) {
                    valueHolder.value = Boolean.parseBoolean(text);
                } else if (type == BigDecimal.class) {
                    valueHolder.value = new BigDecimal(text);
                } else if (type == String.class) {
                    valueHolder.value = text;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return valueHolder.value;
    }
}
