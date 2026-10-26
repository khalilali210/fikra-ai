export default {
  async fetch(request, env) {
    const cors = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "POST, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type",
      "Content-Type": "application/json; charset=utf-8"
    };

    if (request.method === "OPTIONS") {
      return new Response(null, { headers: cors });
    }

    if (request.method !== "POST") {
      return Response.json(
        { error: "استخدم POST لإرسال السؤال" },
        { status: 405, headers: cors }
      );
    }

    try {
      if (!env.GEMINI_API_KEY) {
        throw new Error("مفتاح Gemini غير مضبوط على الخادم");
      }

      const body = await request.json();
      const message = String(body.message || "").trim();

      if (!message) {
        return Response.json(
          { error: "اكتب سؤالاً أولاً" },
          { status: 400, headers: cors }
        );
      }

      const response = await fetch(
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "x-goog-api-key": env.GEMINI_API_KEY
          },
          body: JSON.stringify({
            contents: [{
              parts: [{
                text: "أنت Fikra AI، مساعد ذكي تجيب باللغة العربية بوضوح. سؤال المستخدم: " + message
              }]
            }]
          })
        }
      );

      const data = await response.json();

      if (!response.ok) {
        return Response.json(
          { error: data.error?.message || "تعذر الاتصال بـ Gemini" },
          { status: 502, headers: cors }
        );
      }

      const reply = data.candidates?.[0]?.content?.parts
        ?.map(part => part.text || "")
        .join("")
        .trim();

      return Response.json(
        { reply: reply || "لم يصل رد نصي من Gemini." },
        { headers: cors }
      );
    } catch (error) {
      return Response.json(
        { error: error.message || "حدث خطأ في الخادم" },
        { status: 500, headers: cors }
      );
    }
  }
};
