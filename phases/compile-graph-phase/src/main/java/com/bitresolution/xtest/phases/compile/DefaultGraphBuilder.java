package com.bitresolution.xtest.phases.compile;

import com.bitresolution.succor.reflection.FullyQualifiedClassName;
import com.bitresolution.succor.reflection.FullyQualifiedMethodName;
import com.bitresolution.succor.reflection.PackageName;
import com.bitresolution.xtest.Node;
import com.bitresolution.xtest.phases.compile.nodes.ClassNode;
import com.bitresolution.xtest.phases.compile.nodes.MethodNode;
import com.bitresolution.xtest.phases.compile.nodes.PackageNode;
import com.bitresolution.xtest.phases.compile.nodes.XNode;
import com.bitresolution.xtest.phases.sources.Sources;
import com.google.common.base.Optional;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Set;

import static com.bitresolution.xtest.phases.compile.relationships.RelationshipBuilder.where;
import static org.reflections.ReflectionUtils.withAnnotation;

@Component
public class DefaultGraphBuilder implements GraphBuilder {

    private static final Logger log = LoggerFactory.getLogger(DefaultGraphBuilder.class);

    private final JungTestGraph graph = new JungTestGraph();

    @Override
    @NotNull
    public GraphBuilder add(@NotNull Sources input) throws CompileGraphException {
        for(FullyQualifiedClassName fqcn : input.getClasses()) {
            try {
                Class<?> klass = fqcn.loadClass();

                @SuppressWarnings("unchecked")
                Set<Method> collectedMethods = ReflectionUtils.getAllMethods(klass, withAnnotation(Node.class));

                if(klass.isAnnotationPresent(Node.class) || !collectedMethods.isEmpty()) {
                    XNode<PackageName> packageNode = findOrBuildPackageNode(fqcn, graph.getRootNode());
                    buildClassNode(fqcn, collectedMethods, packageNode);
                }
                else {
                    log.error("Class {} is not an xTest node", fqcn.getFullyQualifiedName());
                }
            }
            catch (ClassNotFoundException e) {
                throw new CompileGraphException("Error loading class: " + fqcn, e);
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private XNode<PackageName> findOrBuildPackageNode(FullyQualifiedClassName fqcn, XNode<?> rootNode) throws CompileGraphException {
        Optional<XNode<PackageName>> node = graph.findNodeByValue(fqcn.getPackageName());
        if(node.isPresent()) {
            return node.get();
        }
        LinkedList<PackageNode> packages = new LinkedList<PackageNode>();
        for(PackageName p = fqcn.getPackageName(); !p.equals(PackageName.DEFAULT); p = p.getParent()) {
            packages.push(new PackageNode(p));
        }
        XNode<?> source = rootNode;
        for(PackageNode dest : packages) {
            graph.addNode(dest);
            graph.addRelationship(where(source).contains(dest));
            source = dest;
        }
        return (XNode<PackageName>) source;
    }

    private void buildClassNode(FullyQualifiedClassName fqcn, Set<Method> annotatedMethods, XNode<?> rootNode) throws CompileGraphException {
        ClassNode classNode = new ClassNode(fqcn);
        graph.addNode(classNode);
        graph.addRelationship(where(rootNode).contains(classNode));
        log.debug("Adding class node for '{}' to graph", fqcn.getFullyQualifiedName());

        for(Method m : annotatedMethods) {
            buildMethodNode(classNode, m);
        }
    }

    private void buildMethodNode(ClassNode classNode, Method m) throws CompileGraphException {
        FullyQualifiedMethodName method = new FullyQualifiedMethodName(m);
        MethodNode methodNode = new MethodNode(method);
        graph.addNode(methodNode);
        graph.addRelationship(where(classNode).contains(methodNode));
        log.debug("Adding method node for '{}' to graph", method.getFullyQualifiedName());
    }

    @Override
    @NotNull
    public TestGraph build() {
        return graph;
    }
}
