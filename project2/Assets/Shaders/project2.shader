Shader "Custom/ShadertoyCable"
{
    Properties
    {
        _Resolution("Resolution", Vector) = (800, 600, 0, 0)
    }

    SubShader
    {
        Tags { "RenderType"="Opaque" }
        Pass
        {
            CGPROGRAM
            #pragma vertex vert
            #pragma fragment frag
            #include "UnityCG.cginc"

            float4 _Resolution;   // (width, height, 0, 0)
            // float4 _Time;         // _Time.y is current time in seconds

            // Equivalent to #define hash(x) fract(sin(x)*43758.5453123)
            inline float hash(float x) 
            {
                return frac(sin(x) * 43758.5453123);
            }

            // Color palette function (not strictly needed in this cable effect but kept for completeness)
            inline float3 pal(float t)
            {
                return 0.5 + 0.5 * cos(6.28 * (1.0 * t + float3(0.0, 0.1, 0.1)));
            }

            // stepNoise function
            inline float stepNoise(float x, float n)
            {
                float factor = 0.3;
                float i = floor(x);
                float f = x - i;
                float u = smoothstep(0.5 - factor, 0.5 + factor, f);

                float res = lerp(floor(hash(i) * n), floor(hash(i + 1.0) * n), u);
                res /= (n - 1.0) * 0.5;
                return res - 1.0;
            }

            // path function for wavy cable
            inline float3 path(float3 p)
            {
                float3 o = float3(0.0, 0.0, 0.0);
                o.x += stepNoise(p.z * 0.05, 5.0) * 5.0;
                o.y += stepNoise(p.z * 0.07, 3.975) * 5.0;
                return o;
            }

            // diam2 function
            inline float diam2(float2 p, float s)
            {
                p = abs(p);
                return (p.x + p.y - s) * rsqrt(3.0);
            }

            // erot: rotate vector p around axis ax by angle t
            inline float3 erot(float3 p, float3 ax, float t)
            {
                // lerp(a,b,x) is the HLSL equivalent of mix(a,b,x)
                return lerp(dot(ax, p) * ax, p, cos(t)) + cross(ax, p) * sin(t);
            }

            struct appdata
            {
                float4 vertex : POSITION;
                float2 uv : TEXCOORD0;
            };

            struct v2f
            {
                float2 uv : TEXCOORD0;
                float4 vertex : SV_POSITION;
            };

            v2f vert(appdata v)
            {
                v2f o;
                o.vertex = UnityObjectToClipPos(v.vertex);
                o.uv = v.uv;
                return o;
            }

            fixed4 frag(v2f i) : SV_Target
            {
                // Shadertoy variables
                float iTime = _Time.y;
                float2 iRes = _Resolution.xy;

                // Convert UV to Shadertoy-style fragCoord
                float2 fragCoord = i.uv * iRes;
                float2 uv = (fragCoord - 0.5 * iRes) / iRes.y;

                float3 col = float3(0.0, 0.0, 0.0);

                // Camera-like setup
                float3 ro = float3(0.0, 0.0, -1.0);
                float3 rt = float3(0.0, 0.0, 0.0);

                // Animate camera along Z
                ro.z += iTime * 5.0;
                rt.z += iTime * 5.0;

                // Distort camera positions
                ro += path(ro);
                rt += path(rt);

                // Build local coordinate axes
                float3 zDir = normalize(rt - ro);
                float3 xDir = float3(zDir.z, 0.0, -zDir.x);

                // Determine the angle for rotating ray direction
                float angle = stepNoise(iTime + hash(uv.x * uv.y * iTime) * 0.05, 6.0);

                // Construct the ray direction
                float3 rd = mul(
                    float3x3(xDir, cross(zDir, xDir), zDir),
                    erot(normalize(float3(uv, 1.0)), float3(0.0, 0.0, 1.0), angle)
                );

                float e = 0.0; // Step size
                float g = 0.0; // Accumulated distance

                // Raymarch loop (using int i for standard HLSL compliance)
                for(int i = 0; i < 99; i++)
                {
                    float fi = (float)i;  // If we need i as float for calculations

                    float3 p = ro + rd * g;
                    p -= path(p);

                    // Repeated transformations
                    float r = 0.0;
                    float3 pp = p;
                    float sc = 1.0;

                    for(int j = 0; j < 4; j++)
                    {
                        float fj = (float)j; // if needed as float
                        r = clamp(
                              r + abs(dot(sin(pp * 3.0), cos(pp.yzx * 2.0)) * 0.3 - 0.1) / sc,
                              -0.5, 0.5
                            );
                        pp = erot(pp, normalize(float3(0.1, 0.2, 0.3)), 0.785 + fj);
                        pp += pp.yzx + fj * 50.0;
                        sc *= 1.5;
                        pp *= 1.5;
                    }

                    float h = abs(diam2(p.xy, 7.0)) - 3.0 - r;

                    // Additional rotation
                    p = erot(p, float3(0.0, 0.0, 1.0), path(p).x * 0.5 + p.z * 0.2);

                    float tVal = length(abs(p.xy) - 0.5) - 0.1;
                    h = min(tVal, h);

                    // The step to move forward in the raymarch
                    e = max(0.001, (tVal == h ? abs(h) : h));
                    g += e;

                    // Check if we hit the cable surface
                    if(tVal == h)
                    {
                        float brightness = 100.0 * exp(-20.0 * frac(p.z * 0.25 + iTime));
                        float modVal = fmod(
                            floor(p.z * 4.0) + fmod(floor(p.y * 4.0), 2.0),
                            2.0
                        );

                        col += float3(0.3, 0.2, 0.1)
                               * brightness
                               * modVal
                               * 0.0325
                               / exp(fi * fi * e);
                    }
                    else
                    {
                        // Ambient/darker contribution
                        col += float3(0.1, 0.1, 0.1)
                               * 0.0325
                               / exp(fi * fi * e);
                    }
                }

                // Final color mix
                col = lerp(col, float3(0.9, 0.9, 1.1), 1.0 - exp(-0.01 * g * g * g));

                return float4(col, 1.0);
            }
            ENDCG
        }
    }

    FallBack "Diffuse"
}
