MERGE INTO roles (name) KEY(name) VALUES ('ROLE_ADMIN');
MERGE INTO roles (name) KEY(name) VALUES ('ROLE_SELLER');
MERGE INTO roles (name) KEY(name) VALUES ('ROLE_BUYER');

MERGE INTO category (name, slug, description) KEY(name) VALUES ('Fiction', 'fiction', 'Novels and short stories');
MERGE INTO category (name, slug, description) KEY(name) VALUES ('Non-Fiction', 'non-fiction', 'Educational and informative books');
MERGE INTO category (name, slug, description) KEY(name) VALUES ('Science & Technology', 'science-technology', 'Science, tech, and computing books');
MERGE INTO category (name, slug, description) KEY(name) VALUES ('History', 'history', 'Historical accounts and biographies');
MERGE INTO category (name, slug, description) KEY(name) VALUES ('Self-Help', 'self-help', 'Personal development and motivation');
MERGE INTO category (name, slug, description) KEY(name) VALUES ('Business', 'business', 'Business, economics, and entrepreneurship');

