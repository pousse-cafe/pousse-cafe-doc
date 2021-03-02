package poussecafe.doc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import poussecafe.domain.ValueObject;

public class Domain implements ValueObject {

    public static class Builder {

        private Domain domain = new Domain();

        public Builder name(String name) {
            domain.name = name;
            return this;
        }

        public Builder version(String version) {
            domain.version = version;
            return this;
        }

        public Builder modules(List<Module> modules) {
            domain.modules = new ArrayList<>(modules);
            return this;
        }

        public Domain build() {
            Objects.requireNonNull(domain.name);
            Objects.requireNonNull(domain.version);
            Objects.requireNonNull(domain.modules);
            return domain;
        }
    }

    private Domain() {

    }

    private String name;

    public String name() {
        return name;
    }

    private String version;

    public String version() {
        return version;
    }

    private List<Module> modules;

    public List<Module> modules() {
        return modules;
    }
}
