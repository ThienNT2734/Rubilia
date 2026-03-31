const responses = [
    {
        keywords: ["rubilia.store là gì", "rubilia.store của ai", "rubilia là dự án gì", "rubilia là gì", "ai làm rubilia"],
        reply: "Rubilia.store là dự án thuộc đồ án cơ sở ngành Công nghệ Thông tin của nhóm 3 sinh viên: Nguyễn Trung Thiện, Huỳnh Đức Đạt, Phạm Thị Thúy Hân. Trang web cung cấp các sản phẩm làm đẹp như trang điểm, chăm sóc da, và hơn thế nữa!"
    },
    {
        keywords: ["liên hệ với rubilia", "email của rubilia", "số điện thoại rubilia", "cách liên hệ rubilia", "liên hệ rubilia", "liên hệ như thế nào", "liên hệ", "liên hệ với ai", "liên hệ ở đâu", "liên lạc", "hotline rubilia", "email rubilia", "phone rubilia"],
        reply: "Bạn có thể liên hệ với Rubilia.store qua email: thiennguyen2734@gmail.com hoặc thieb27032004@gmail.com. Số điện thoại: 0367149710 và 0367149735."
    },
    {
        keywords: ["cửa hàng rubilia nằm ở đâu", "địa chỉ rubilia.store", "cửa hàng ở đâu", "rubilia ở đâu", "địa chỉ cửa hàng", "địa chỉ rubilia", "cửa hàng rubilia ở đâu", "địa chỉ", "rubilia nằm ở đâu", "ở đâu"],
        reply: "Cửa hàng Rubilia nằm tại 59 đường số 4, An Phú, Thủ Đức, TP.HCM."
    },
    {
        keywords: ["flash sale", "flashsale", "khi nào", "bao giờ", "lúc nào", "flash sale diễn ra lúc nào"],
        reply: "Flash sale diễn ra hàng ngày, kết thúc sau 12 tiếng. Bạn có thể xem các sản phẩm flash sale trên trang chủ!"
    },
    {
        keywords: ["flash sale", "flashsale", "có những sản phẩm gì", "sản phẩm gì", "san pham gi", "flash sale có gì", "sản phẩm flash sale"],
        reply: "Hiện tại tôi không thể truy vấn danh sách sản phẩm flash sale. Bạn có thể xem trực tiếp trên trang chủ rubilia.store nhé!"
    },
    {
        keywords: ["chăm sóc da", "cham soc da", "có", "gì", "chăm sóc da có gì", "sản phẩm chăm sóc da"],
        reply: "Chúng tôi có nhiều sản phẩm chăm sóc da như sữa rửa mặt, toner, kem dưỡng. Bạn có thể xem chi tiết ở khu vực Chăm Sóc Da!"
    },
    {
        keywords: ["trang điểm", "trang diem", "son", "phấn", "trang điểm có gì", "sản phẩm trang điểm"],
        reply: "Chúng tôi có nhiều sản phẩm trang điểm như son, phấn, bảng màu mắt. Bạn có thể xem chi tiết ở khu vực Trang Điểm!"
    },
    {
        keywords: ["chăm sóc cơ thể", "cham soc co the", "sữa tắm", "chăm sóc cơ thể có gì", "sản phẩm chăm sóc cơ thể"],
        reply: "Chúng tôi có nhiều sản phẩm chăm sóc cơ thể như sữa tắm, tẩy da chết. Bạn có thể xem chi tiết ở khu vực Chăm Sóc Cơ Thể!"
    },
    {
        keywords: ["mặt nạ", "mat na", "dưỡng da", "mặt nạ có gì", "sản phẩm mặt nạ"],
        reply: "Chúng tôi có nhiều loại mặt nạ dưỡng da, mặt nạ đất sét. Bạn có thể xem chi tiết ở khu vực Mặt Nạ!"
    },
    {
        keywords: ["phụ kiện", "phu kien", "có", "gì", "phụ kiện có gì", "sản phẩm phụ kiện"],
        reply: "Chúng tôi có nhiều phụ kiện làm đẹp như cọ trang điểm, bông tẩy trang. Bạn có thể xem chi tiết ở khu vực Phụ Kiện!"
    },
    {
        keywords: ["deal", "khuyến mãi", "deal chào hè", "deal khung", "deal có gì", "khuyến mãi có gì", "deal rubilia", "khuyến mãi rubilia"],
        reply: "Hiện tại có Deal Khủng Chào Hè với nhiều ưu đãi hấp dẫn. Xem ngay trên trang chủ nhé!"
    },
    {
        keywords: ["mua hàng", "thanh toán", "giỏ hàng", "mua trên rubilia", "mua hàng thế nào", "thanh toán thế nào", "đặt hàng"],
        reply: "Bạn có thể chọn sản phẩm, thêm vào giỏ hàng, và tiến hành thanh toán tại trang Checkout. Nếu cần hỗ trợ, hãy liên hệ qua email hoặc điện thoại!"
    },
    {
        keywords: ["giao hàng", "ship hàng", "như thế nào", "giao hàng thế nào", "ship hàng ra sao", "ship hàng bao lâu"],
        reply: "Rubilia.store hỗ trợ giao hàng toàn quốc. Sau khi đặt hàng, chúng tôi sẽ xử lý và giao hàng trong vòng 3-5 ngày làm việc. Bạn có thể liên hệ để biết thêm chi tiết!"
    },
    {
        keywords: ["đổi hàng", "trả hàng", "thế nào", "đổi hàng thế nào", "trả hàng ra sao", "chính sách đổi trả"],
        reply: "Rubilia.store hỗ trợ đổi trả hàng trong vòng 7 ngày nếu sản phẩm lỗi hoặc không đúng mô tả. Vui lòng liên hệ qua email hoặc số điện thoại để được hỗ trợ!"
    },
    {
        keywords: ["tạo tài khoản", "đăng ký tài khoản", "như thế nào", "tạo tài khoản thế nào", "đăng ký rubilia"],
        reply: "Bạn có thể đăng ký tài khoản trên rubilia.store bằng cách vào mục \"Đăng ký\", điền thông tin cá nhân và xác nhận email. Nếu gặp khó khăn, hãy liên hệ với chúng tôi!"
    },
    {
        keywords: ["quên mật khẩu", "khôi phục mật khẩu", "làm sao", "quên mật khẩu thế nào", "khôi phục mật khẩu ra sao"],
        reply: "Bạn có thể khôi phục mật khẩu bằng cách vào mục \"Quên mật khẩu\" trên trang đăng nhập, nhập email và làm theo hướng dẫn. Nếu cần hỗ trợ, hãy liên hệ với chúng tôi!"
    },
    {
        keywords: ["rubilia bán gì", "rubilia có sản phẩm gì", "sản phẩm của rubilia", "rubilia có gì"],
        reply: "Rubilia.store bán các sản phẩm làm đẹp bao gồm trang điểm, chăm sóc da, chăm sóc cơ thể, mặt nạ, và phụ kiện. Bạn có thể xem chi tiết trên trang chủ!"
    },
    {
        keywords: ["chất lượng sản phẩm rubilia", "sản phẩm rubilia có tốt không", "chất lượng rubilia", "sản phẩm rubilia tốt không"],
        reply: "Sản phẩm của Rubilia.store đều được chọn lọc kỹ càng, đảm bảo chất lượng cao và an toàn cho người dùng. Bạn có thể yên tâm mua sắm!"
    },
    {
        keywords: ["rubilia có uy tín không", "rubilia có đáng tin không", "uy tín rubilia", "rubilia đáng tin không"],
        reply: "Rubilia.store là dự án của nhóm sinh viên ngành Công nghệ Thông tin, chúng tôi cam kết cung cấp sản phẩm chất lượng và dịch vụ tận tâm. Bạn có thể liên hệ để được hỗ trợ nếu cần!"
    },
    {
        keywords: ["cửa hàng mở cửa lúc mấy giờ", "giờ mở cửa rubilia", "rubilia mở cửa lúc nào", "giờ mở cửa", "mở cửa lúc mấy giờ", "cửa hàng mở cửa"],
        reply: "Cửa hàng Rubilia mở cửa từ 9:00 sáng đến 9:00 tối hàng ngày. Bạn có thể ghé thăm tại 59 đường số 4, An Phú, Thủ Đức, TP.HCM!"
    },
    {
        keywords: ["chính sách bảo hành", "bảo hành rubilia", "bảo hành thế nào", "rubilia bảo hành ra sao", "bảo hành sản phẩm"],
        reply: "Rubilia.store hỗ trợ bảo hành sản phẩm trong vòng 30 ngày đối với các lỗi kỹ thuật từ nhà sản xuất. Vui lòng liên hệ qua email hoặc số điện thoại để được hỗ trợ!"
    },
    {
        keywords: ["son tint romand", "son romand", "romand the juicy lasting tint"],
        reply: "Son Tint Lì Căng Bóng Romand The Juicy Lasting Tint hiện có sẵn tại Rubilia.store. Bạn có thể xem chi tiết và đặt mua ở khu vực Trang Điểm!"
    },
    {
        keywords: ["kem chống nắng dr.g", "kem chống nắng drg", "dr.g brightening up sun"],
        reply: "Kem Chống Nắng Dưỡng Sáng Da Dr.G Brightening Up Sun+ SPF50+ PA+++ hiện có sẵn tại Rubilia.store. Bạn có thể xem chi tiết và đặt mua ở khu vực Chăm Sóc Da!"
    },
    {
        keywords: ["mặt nạ đất sét", "mặt nạ re:p", "re:p bio fresh mask"],
        reply: "Mặt Nạ Đất Sét Thu Nhỏ Lỗ Chân Lông Re:p Bio Fresh Mask With Real Nutrition Herbs hiện có sẵn tại Rubilia.store. Bạn có thể xem chi tiết và đặt mua ở khu vực Mặt Nạ!"
    },
    {
        keywords: ["sữa tắm tesori", "tesori d'oriente", "sữa tắm nước hoa"],
        reply: "Sữa Tắm Hương Nước Hoa Ý Cao Cấp Tesori d'Oriente Aromatic Bath Cream hiện có sẵn tại Rubilia.store. Bạn có thể xem chi tiết và đặt mua ở khu vực Chăm Sóc Cơ Thể!"
    }
];

const handleSendMessage = (message, setMessages, faqQuestions) => {
    setMessages(prev => [...prev, { role: 'user', content: message }]);

    const messageLower = message.toLowerCase();
    let botResponse = "Xin lỗi, tôi chỉ trả lời các câu hỏi liên quan đến dự án Rubilia. Bạn có thể hỏi về sản phẩm, mua hàng, hoặc liên hệ nhé!";

    for (const response of responses) {
        const found = response.keywords.some(keyword => messageLower.includes(keyword.toLowerCase()));
        if (found) {
            botResponse = response.reply;
            break;
        }
    }

    if (faqQuestions.includes(message)) {
        if (message === "Flash sale diễn ra khi nào?") {
            botResponse = "Flash sale diễn ra hàng ngày, kết thúc sau 12 tiếng. Bạn có thể xem các sản phẩm flash sale trên trang chủ!";
        } else if (message === "Làm sao để mua hàng trên Rubilia.store?") {
            botResponse = "Bạn có thể chọn sản phẩm, thêm vào giỏ hàng, và tiến hành thanh toán tại trang Checkout. Nếu cần hỗ trợ, hãy liên hệ qua email hoặc điện thoại!";
        } else if (message === "Rubilia.store là trang web của ai?") {
            botResponse = "Rubilia.store là dự án thuộc đồ án cơ sở ngành Công nghệ Thông tin của nhóm 3 sinh viên: Nguyễn Trung Thiện, Huỳnh Đức Đạt, Phạm Thị Thúy Hân. Trang web cung cấp các sản phẩm làm đẹp như trang điểm, chăm sóc da, và hơn thế nữa!";
        } else if (message === "Liên hệ với Rubilia.store như thế nào?") {
            botResponse = "Bạn có thể liên hệ với Rubilia.store qua email: thiennguyen2734@gmail.com hoặc thieb27032004@gmail.com. Số điện thoại: 0367149710 và 0367149735.";
        } else if (message === "Cửa hàng Rubilia nằm ở đâu?") {
            botResponse = "Cửa hàng Rubilia nằm tại 59 đường số 4, An Phú, Thủ Đức, TP.HCM.";
        } else if (message === "Cửa hàng mở cửa lúc mấy giờ?") {
            botResponse = "Cửa hàng Rubilia mở cửa từ 9:00 sáng đến 9:00 tối hàng ngày. Bạn có thể ghé thăm tại 59 đường số 4, An Phú, Thủ Đức, TP.HCM!";
        } else if (message === "Chính sách bảo hành của Rubilia thế nào?") {
            botResponse = "Rubilia.store hỗ trợ bảo hành sản phẩm trong vòng 30 ngày đối với các lỗi kỹ thuật từ nhà sản xuất. Vui lòng liên hệ qua email hoặc số điện thoại để được hỗ trợ!";
        }
    }

    setMessages(prev => [...prev, { role: 'bot', content: botResponse }]);
};

export { responses, handleSendMessage };