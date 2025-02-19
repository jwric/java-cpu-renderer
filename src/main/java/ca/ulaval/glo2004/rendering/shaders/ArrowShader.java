package ca.ulaval.glo2004.rendering.shaders;

import ca.ulaval.glo2004.rendering.Texture;
import ca.ulaval.glo2004.rendering.Vertex;
import ca.ulaval.glo2004.rendering.awt.AWTARGBTexture;
import ca.ulaval.glo2004.rendering.pipeline.VSOutput;
import ca.ulaval.glo2004.rendering.pipeline.Varyings;
import ca.ulaval.glo2004.rendering.pipeline.VertexAttributes;
import ca.ulaval.glo2004.rendering.utils.ColorUtils;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;
import ca.ulaval.glo2004.util.math.Vec4;

public class ArrowShader extends Shader {

    static final int IN_POSITION_OFFSET = 0;
    static final Attribute in_position = new Attribute(IN_POSITION_OFFSET, DataType.FLOAT3);
    static final int IN_UV_OFFSET = 3;
    static final Attribute in_uv = new Attribute(IN_UV_OFFSET, DataType.FLOAT2);
    static final int IN_NORMAL_OFFSET = 5;
    static final Attribute in_normal = new Attribute(IN_NORMAL_OFFSET, DataType.FLOAT3);

    static final int OUT_UV_OFFSET = 0;
    static final Attribute out_uv = new Attribute(OUT_UV_OFFSET, DataType.FLOAT2);
    static final int OUT_NORMAL_OFFSET = 2;
    static final Attribute out_normal = new Attribute(OUT_NORMAL_OFFSET, DataType.FLOAT3);


    static Texture sampler = AWTARGBTexture.createFromFile("arrowButtonDown2.png");

    public Vec4 colorMult = new Vec4(1,1,1,1);

    @Override
    public VSOutput vertex(Vertex vertex) {
        VSOutput v = new VSOutput();
        v.position = projectionMatrix.transform(viewMatrix.transform(modelMatrix.transform(vertex.position.toVec4())));
        v.uv = vertex.uv;
        v.normal = normalMatrix.transform(vertex.normal.toVec4()).toVec3().normalized();
        v.color = vertex.color;
        return v;
    }

    @Override
    public void vertex(final VertexAttributes in, final Vec4 gl_Position, final VertexAttributes out) {
        Vec3 position = in.getFloat3(0);
        Vec2 uv = in.getFloat2(3);
        Vec3 normal = in.getFloat3(5);

        Vec4 newPosition = projectionMatrix.multiply(viewMatrix.multiply(modelMatrix)).transform(position.toVec4());
        Vec3 newNormal = normalMatrix.transform(normal.toVec4()).toVec3().normalized();
        out.putFloat2(0, uv);
        out.putFloat3(2, newNormal);

        gl_Position.set(newPosition);
    }

    @Override
    public int fragment(Vec4 fragCoord, Varyings varyings) {
        return 0;
    }

    @Override
    public VertexAttributes createInputVertexAttribs() {
        return new VertexAttributes(in_position.dataType.size + in_uv.dataType.size + in_normal.dataType.size);
    }

    @Override
    public VertexAttributes createOutputVertexAttribs() {
        return new VertexAttributes(out_uv.dataType.size + out_normal.dataType.size);
    }

    @Override
    public void fragment(Vec4 gl_FragCoord, final VertexAttributes out, final Vec4 gl_FragColor) {
        Vec2 uv = out.getFloat2(OUT_UV_OFFSET);


        int sample = sampler.sample(uv);
        ColorUtils.vec4(sample, gl_FragColor);
        gl_FragColor.mult(new Vec4(1, 1, 1, (float) ColorUtils.getAlpha(baseColor) /255f));
        gl_FragColor.mult(colorMult);
    }
}
