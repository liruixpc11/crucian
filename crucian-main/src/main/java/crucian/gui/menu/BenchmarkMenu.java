package crucian.gui.menu;

import crucian.benchmark.*;
import vnreal.ToolKit;
import vnreal.gui.GUI;
import vnreal.network.NetworkStack;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiRuiNet
 *         14-2-28 下午3:24
 */
public class BenchmarkMenu extends DynamicClassMenu {
    private static abstract class Stub<T, AT extends Annotation, PT extends Annotation> {
        String name;
        Class<? extends T> type;
        Method factoryMethod;
        List<ParamInfo> params = new ArrayList<ParamInfo>();
        String defaultValue = "";

        private Class<T> tClass;

        @SuppressWarnings("unchecked")
        private Class<T> getTClass() {
            if (tClass == null) {
                tClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            }

            return tClass;
        }

        private Class<AT> atClass;

        @SuppressWarnings("unchecked")
        private Class<AT> getAtClass() {
            if (atClass == null) {
                atClass = (Class<AT>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
            }

            return atClass;
        }

        private Class<PT> ptClass;

        @SuppressWarnings("unchecked")
        private Class<PT> getPtClass() {
            if (ptClass == null) {
                ptClass = (Class<PT>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
            }

            return ptClass;
        }

        @SuppressWarnings("unchecked")
        private Stub(Class<? extends T> type, Method factoryMethod) {
            this.type = type;
            this.factoryMethod = factoryMethod;

            AT action = type.getAnnotation(getAtClass());
            if (action != null) {
                this.name = getAtName(action);
            }

            for (int i = 0; i < factoryMethod.getParameterTypes().length; i++) {
                Class<?> paramType = factoryMethod.getParameterTypes()[i];
                String name = Integer.toString(i);
                for (Annotation annotation : factoryMethod.getParameterAnnotations()[i]) {
                    if (annotation.annotationType() == getPtClass()) {
                        name = getPtName((PT) annotation);
                    } else if (annotation.annotationType() == DefaultValue.class) {
                        defaultValue = ((DefaultValue) annotation).value();
                    }
                }

                params.add(new ParamInfo(name, paramType, defaultValue));
            }
        }

        @SuppressWarnings("unchecked")
        T createInstance() throws CancelException {
            Object[] params = queryParams();
            if (params == null) {
                throw new CancelException();
            }

            try {
                return (T) factoryMethod.invoke(type, params);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private Object[] queryParams() throws CancelException {
            ParamDialog paramDialog = new ParamDialog(GUI.getInstance(), params, name, getType());
            if (paramDialog.show()) {
                return paramDialog.getValues();
            }

            throw new CancelException();
        }

        protected abstract String getPtName(PT annotation);

        protected abstract String getAtName(AT annotation);

        protected abstract String getType();
    }

    private static class CancelException extends Exception {
    }

    private static class BenchmarkStub extends Stub<Benchmark, BenchmarkAction, BenchmarkParam> {
        private BenchmarkStub(Class<? extends Benchmark> type, Method factoryMethod) {
            super(type, factoryMethod);
        }

        @Override
        protected String getPtName(BenchmarkParam annotation) {
            return annotation.value();
        }

        @Override
        protected String getAtName(BenchmarkAction annotation) {
            return annotation.value();
        }

        @Override
        protected String getType() {
            return "Benchmark";
        }
    }

    private static class AlgorithmStub extends Stub<MappingAlgorithm, AlgorithmAction, AlgorithmParam> {
        private AlgorithmStub(Class<? extends MappingAlgorithm> type, Method factoryMethod) {
            super(type, factoryMethod);
        }

        @Override
        protected String getPtName(AlgorithmParam annotation) {
            return annotation.value();
        }

        @Override
        protected String getAtName(AlgorithmAction annotation) {
            return annotation.value();
        }

        @Override
        protected String getType() {
            return "Algorithm";
        }
    }

    private List<BenchmarkStub> benchmarkStubs = new ArrayList<BenchmarkStub>();
    private List<AlgorithmStub> algorithmStubs = new ArrayList<AlgorithmStub>();

    public BenchmarkMenu() {
        super("基准测试", "crucian.benchmark.actions");
        add(new JSeparator());

        scanBenchmarks();
        scanAlgorithms();

        for (final BenchmarkStub benchmarkStub : benchmarkStubs) {
            JMenu benchmarkMenu = new JMenu("Benchmark " + benchmarkStub.name);
            add(benchmarkMenu);
            for (final AlgorithmStub algorithmStub : algorithmStubs) {
                JMenuItem menuItem = new JMenuItem("Algorithm " + algorithmStub.name);
                benchmarkMenu.add(menuItem);
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        StatusReporter reporter = new ConsoleReporter();
                        try {
                            Benchmark benchmark = benchmarkStub.createInstance();
                            if (benchmark.isNeedScenario()) {
                                NetworkStack networks = ToolKit.getScenario().getNetworkStack();
                                if (networks == null) {
                                    JOptionPane.showMessageDialog(GUI.getInstance(), "网络栈为空，请导入", "错的", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                benchmark.setScenario(networks);
                            }

                            benchmark.evaluate(algorithmStub.createInstance()).reportStatus(reporter);
                        } catch (CancelException ignored) {
                        } catch (RuntimeException ex) {
                            reporter.report(ex);
                        }
                    }
                });
            }
        }
    }

    private void scanAlgorithms() {
        for (Class<? extends MappingAlgorithm> algorithmType : DynamicClassMenu.listClasses("crucian.benchmark.algorithms", MappingAlgorithm.class)) {
            Method factory = queryFactory(algorithmType, AlgorithmFactory.class);
            if (factory == null) continue;

            algorithmStubs.add(new AlgorithmStub(algorithmType, factory));
        }
    }

    private void scanBenchmarks() {
        for (Class<? extends Benchmark> benchmarkType : DynamicClassMenu.listClasses("crucian.benchmark.benchmarks", Benchmark.class)) {
            Method factory = queryFactory(benchmarkType, BenchmarkFactory.class);
            if (factory == null) continue;

            benchmarkStubs.add(new BenchmarkStub(benchmarkType, factory));
        }
    }

    private Method queryFactory(Class<?> type, Class<? extends Annotation> annotationType) {
        for (Method method : type.getMethods()) {
            int modifier = method.getModifiers();
            if (Modifier.isStatic(modifier) &&
                    Modifier.isPublic(modifier) &&
                    method.isAnnotationPresent(annotationType)) {
                return method;
            }
        }

        return null;
    }

}

