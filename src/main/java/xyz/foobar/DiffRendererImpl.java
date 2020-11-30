package xyz.foobar;

import java.util.List;

public class DiffRendererImpl implements DiffRenderer {

    public String render(Diff<?> diff) throws DiffException {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            Class classs = getClasss(diff);

            List<Differences> differencesList = diff.getDifferencesList();

            for (Differences differences : differencesList) {
                levelSpace(stringBuilder, differences.getLevel());
                if (classs == null) {
                    stringBuilder.append(differences.getAction()).append(":").append(differences.getFieldName());
                } else if (differences.getFieldName().equals(classs.getSimpleName()) && differences.isRootElem()) {
                    stringBuilder.append(differences.getAction()).append(":").append(classs.getSimpleName()).append("\n");
                } else {
                    if (differences.getAction().equalsIgnoreCase("Update")) {
                        stringBuilder.append(differences.getAction()).append(":").append((differences.getFieldName())).append(" from ").append(differences.getPrevValue()).append(" to ").append(differences.getValue()).append("\n");
                    } else if (differences.getAction().equalsIgnoreCase("Delete")) {
                        stringBuilder.append(differences.getAction()).append(":").append((differences.getFieldName())).append("\n");
                    } else { // Create
                        stringBuilder.append(differences.getAction()).append(":").append((differences.getFieldName())).append(" as ").append(differences.getValue()).append("\n");
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("Trace >>" +stringBuilder.toString() +"<<");
        return stringBuilder.toString();
    }

    private Class getClasss(Diff<?> diff) {
        Class aClass;
        if (diff.getVal() != null) {
            aClass = diff.getVal().getClass();
        } else {
            aClass = null;
        }
        return aClass;
    }

    private void levelSpace(StringBuilder stringBuilder, int level) {
        for (int j = 0; j < level; j++) {
            stringBuilder.append("   ");
        }
    }

    public static void main(String args[]) {
        DiffRendererImpl diffRenderer = new DiffRendererImpl();
    }

}
