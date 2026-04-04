-- Initialize default categories
INSERT INTO category(name, slug, description) VALUES ('Fiction', 'fiction', 'Novels and short stories') ON CONFLICT (name) DO NOTHING;
INSERT INTO category(name, slug, description) VALUES ('Non-Fiction', 'non-fiction', 'Educational and informative books') ON CONFLICT (name) DO NOTHING;
INSERT INTO category(name, slug, description) VALUES ('Science & Technology', 'science-technology', 'Science, tech, and computing books') ON CONFLICT (name) DO NOTHING;
INSERT INTO category(name, slug, description) VALUES ('History', 'history', 'Historical accounts and biographies') ON CONFLICT (name) DO NOTHING;
INSERT INTO category(name, slug, description) VALUES ('Self-Help', 'self-help', 'Personal development and motivation') ON CONFLICT (name) DO NOTHING;
INSERT INTO category(name, slug, description) VALUES ('Business', 'business', 'Business, economics, and entrepreneurship') ON CONFLICT (name) DO NOTHING;

